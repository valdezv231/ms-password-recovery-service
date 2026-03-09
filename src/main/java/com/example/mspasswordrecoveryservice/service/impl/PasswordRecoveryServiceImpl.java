package com.example.mspasswordrecoveryservice.service.impl;

import com.example.mspasswordrecoveryservice.config.AppConfig;
import com.example.mspasswordrecoveryservice.dto.ConfirmReset;
import com.example.mspasswordrecoveryservice.dto.PasswordResetRequest;
import com.example.mspasswordrecoveryservice.dto.TokenValidationResponse;
import com.example.mspasswordrecoveryservice.exception.InvalidTokenException;
import com.example.mspasswordrecoveryservice.exception.TokenNotFoundException;
import com.example.mspasswordrecoveryservice.model.PasswordResetToken;
import com.example.mspasswordrecoveryservice.repository.PasswordResetTokenRepository;
import com.example.mspasswordrecoveryservice.service.EmailService;
import com.example.mspasswordrecoveryservice.service.PasswordRecoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private final PasswordResetTokenRepository repository;
    private final EmailService emailService;
    private final AppConfig appConfig;

    @Override
    public Mono<Void> requestReset(PasswordResetRequest dto) {
        log.info("Solicitud de restablecimiento de contraseña para el correo: {}", dto.getEmail());
        return createToken(dto.getEmail())
                .flatMap(this::sendResetEmail);
    }

    @Override
    public Mono<TokenValidationResponse> validateToken(String token) {
        log.debug("Validando token: {}", token);
        return repository.findByToken(token)
                .switchIfEmpty(Mono.error(new TokenNotFoundException("Token not found")))
                .filter(this::isValid)
                .switchIfEmpty(Mono.error(new InvalidTokenException("Token expired or already used")))
                .map(t -> {
                    log.info("Token validado exitosamente para el correo: {}", t.getEmail());
                    return TokenValidationResponse.builder().valid(true).build();
                });
    }

    @Override
    public Mono<Void> markAsUsed(ConfirmReset dto) {
        log.info("Marcando token como usado: {}", dto.getToken());
        return repository.findByToken(dto.getToken())
                .switchIfEmpty(Mono.error(new TokenNotFoundException("Token not found")))
                .flatMap(t -> {
                    log.debug("Estableciendo usado=true para el token asociado al correo: {}", t.getEmail());
                    t.setUsed(true);
                    return repository.save(t);
                })
                .then();
    }

    private Mono<PasswordResetToken> createToken(String email) {
        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .token(UUID.randomUUID().toString())
                .expiration(LocalDateTime.now().plusMinutes(appConfig.getExpiration()))
                .used(false)
                .build();

        log.debug("Creando token para el correo: {}, expira en: {}", email, token.getExpiration());
        return repository.save(token);
    }

    private Mono<Void> sendResetEmail(PasswordResetToken token) {
        log.info("Enviando correo de restablecimiento a: {}", token.getEmail());
        return emailService.send(token.getEmail(), buildResetLink(token.getToken()));
    }

    private boolean isValid(PasswordResetToken token) {
        boolean valid = Boolean.FALSE.equals(token.getUsed())
                && token.getExpiration().isAfter(LocalDateTime.now());
        if (!valid) {
            log.warn("Validación de token fallida para el correo: {} — usado: {}, expiración: {}",
                    token.getEmail(), token.getUsed(), token.getExpiration());
        }
        return valid;
    }

    private String buildResetLink(String token) {
        return "https://frontend/reset-password?token=" + token;
    }
}
