package com.example.mspasswordrecoveryservice.service.impl;

import com.example.mspasswordrecoveryservice.config.AppConfig;
import com.example.mspasswordrecoveryservice.exception.InvalidTokenException;
import com.example.mspasswordrecoveryservice.exception.TokenNotFoundException;
import com.example.mspasswordrecoveryservice.model.PasswordResetToken;
import com.example.mspasswordrecoveryservice.repository.PasswordResetTokenRepository;
import com.example.mspasswordrecoveryservice.service.EmailService;
import com.example.mspasswordrecoveryservice.service.PasswordRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {

    private static final long EXPIRATION_MINUTES = 15;

    private final AppConfig config;

    private final PasswordResetTokenRepository repository;
    private final EmailService emailService;


    @Override
    public Mono<Void> requestReset(String email) {
        return createToken(email)
                .flatMap(this::sendResetEmail);
    }

    @Override
    public Mono<Boolean> validateToken(String token) {

        return repository.findByToken(token)
                .switchIfEmpty(Mono.error(
                        new TokenNotFoundException("Token not found")
                ))
                .filter(this::isValid)
                .switchIfEmpty(Mono.error(
                        new InvalidTokenException("Token expired or already used")
                ))
                .map(t -> true);
    }

    @Override
    public Mono<Void> markAsUsed(String token) {
        return repository.findByToken(token)
                .flatMap(t -> {
                    t.setUsed(true);
                    return repository.save(t);
                })
                .then();
    }

    private Mono<PasswordResetToken> createToken(String email) {
        PasswordResetToken entity = new PasswordResetToken();
        entity.setId(UUID.randomUUID());
        entity.setEmail(email);
        entity.setToken(UUID.randomUUID().toString());
        entity.setExpiration(java.time.LocalDateTime.now().plusMinutes(config.getExpiration()));
        entity.setUsed(false);

        return repository.save(entity);
    }

    private Mono<Void> sendResetEmail(PasswordResetToken token) {
        String link = buildResetLink(token.getToken());
        return emailService.send(token.getEmail(), link);
    }

    private boolean isValid(PasswordResetToken token) {
        return !token.getUsed()
                && token.getExpiration().isAfter(LocalDateTime.now());
    }

    private String buildResetLink(String token) {
        return "https://frontend/reset-password?token=" + token;
    }




}
