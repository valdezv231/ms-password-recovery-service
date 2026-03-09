package com.example.mspasswordrecoveryservice.controller;

import com.example.mspasswordrecoveryservice.dto.ConfirmReset;
import com.example.mspasswordrecoveryservice.dto.PasswordResetRequest;
import com.example.mspasswordrecoveryservice.dto.TokenValidationResponse;
import com.example.mspasswordrecoveryservice.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordRecoveryController {

    private final PasswordRecoveryService service;

    @PostMapping("/request")
    public Mono<Void> request(@Valid @RequestBody PasswordResetRequest dto) {
        return service.requestReset(dto);
    }

    @GetMapping("/validate")
    public Mono<TokenValidationResponse> validate(@RequestParam String token) {
        return service.validateToken(token);
    }

    @PostMapping("/confirm")
    public Mono<Void> confirm(@Valid @RequestBody ConfirmReset dto) {
        return service.markAsUsed(dto);
    }
}
