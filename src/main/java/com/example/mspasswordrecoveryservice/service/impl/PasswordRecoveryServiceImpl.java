package com.example.mspasswordrecoveryservice.service.impl;

import com.example.mspasswordrecoveryservice.config.AppConfig;
import com.example.mspasswordrecoveryservice.dto.ConfirmResetDTO;
import com.example.mspasswordrecoveryservice.dto.PasswordResetRequestDTO;
import com.example.mspasswordrecoveryservice.dto.TokenValidationResponseDTO;
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
    public Mono<Void> requestReset(PasswordResetRequestDTO dto) {
        log.info("Password reset requested for email: {}", dto.getEmail());
        return createToken(dto.getEmail())
                .flatMap(this::sendResetEmail);
    }

    @Override
    public Mono<TokenValidationResponseDTO> validateToken(String token) {
        log.debug("Validating token: {}", token);
        return repository.findByToken(token)
                .switchIfEmpty(Mono.error(new TokenNotFoundException("Token not found")))
                .filter(this::isValid)
                .switchIfEmpty(Mono.error(new InvalidTokenException("Token expired or already used")))
                .map(t -> {
                    log.info("Token validated successfully for email: {}", t.getEmail());
                    return TokenValidationResponseDTO.builder().valid(true).build();
                });
    }

    @Override
    public Mono<Void> markAsUsed(ConfirmResetDTO dto) {
        log.info("Marking token as used: {}", dto.getToken());
        return repository.findByToken(dto.getToken())
                .switchIfEmpty(Mono.error(new TokenNotFoundException("Token not found")))
                .flatMap(t -> {
                    log.debug("Setting used=true for token associated with email: {}", t.getEmail());
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

        log.debug("Creating token for email: {}, expires at: {}", email, token.getExpiration());
        return repository.save(token);
    }

    private Mono<Void> sendResetEmail(PasswordResetToken token) {
        log.info("Sending reset email to: {}", token.getEmail());
        return emailService.send(token.getEmail(), buildResetLink(token.getToken()));
    }

    private boolean isValid(PasswordResetToken token) {
        boolean valid = Boolean.FALSE.equals(token.getUsed())
                && token.getExpiration().isAfter(LocalDateTime.now());
        if (!valid) {
            log.warn("Token validation failed for email: {} — used: {}, expiration: {}",
                    token.getEmail(), token.getUsed(), token.getExpiration());
        }
        return valid;
    }

    private String buildResetLink(String token) {
        return "https://frontend/reset-password?token=" + token;
    }
}
