package com.example.mspasswordrecoveryservice.service;

import com.example.mspasswordrecoveryservice.dto.ConfirmResetDTO;
import com.example.mspasswordrecoveryservice.dto.PasswordResetRequestDTO;
import com.example.mspasswordrecoveryservice.dto.TokenValidationResponseDTO;
import reactor.core.publisher.Mono;

public interface PasswordRecoveryService {

    Mono<Void> requestReset(PasswordResetRequestDTO dto);

    Mono<TokenValidationResponseDTO> validateToken(String token);

    Mono<Void> markAsUsed(ConfirmResetDTO dto);

}
