package com.example.mspasswordrecoveryservice.repository;

import com.example.mspasswordrecoveryservice.model.PasswordResetToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PasswordResetTokenRepository extends ReactiveCrudRepository<PasswordResetToken, UUID> {

    Mono<PasswordResetToken> findByToken(String token);
}
