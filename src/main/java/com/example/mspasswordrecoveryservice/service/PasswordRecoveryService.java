package com.example.mspasswordrecoveryservice.service;

import reactor.core.publisher.Mono;

public interface PasswordRecoveryService {

    Mono<Void> requestReset(String email);

    Mono<Boolean> validateToken(String token);

    Mono<Void> markAsUsed(String token);


}
