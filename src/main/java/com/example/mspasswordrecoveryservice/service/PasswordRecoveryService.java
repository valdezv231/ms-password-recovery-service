package com.example.mspasswordrecoveryservice.service;

import com.example.mspasswordrecoveryservice.dto.ConfirmReset;
import com.example.mspasswordrecoveryservice.dto.PasswordResetRequest;
import com.example.mspasswordrecoveryservice.dto.TokenValidationResponse;
import reactor.core.publisher.Mono;

public interface PasswordRecoveryService {

    Mono<Void> requestReset(PasswordResetRequest dto);

    Mono<TokenValidationResponse> validateToken(String token);

    Mono<Void> markAsUsed(ConfirmReset dto);

}
