package com.example.mspasswordrecoveryservice.controller;

import com.example.mspasswordrecoveryservice.service.PasswordRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordRecoveryController {

    private final PasswordRecoveryService service;

    @PostMapping("/request")
    public Mono<Void> request(@RequestParam String email) {
        return service.requestReset(email);
    }

    @GetMapping("/validate")
    public  Mono<Boolean> validate(@RequestParam String token) {
        return service.validateToken(token);
    }

    @PostMapping("/confirm")
    public Mono<Void> confirm(@RequestParam String token) {
        return service.markAsUsed(token);
    }
}
