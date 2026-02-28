package com.example.mspasswordrecoveryservice.service.impl;

import com.example.mspasswordrecoveryservice.service.EmailService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public Mono<Void> send(String to, String link) {
        return Mono.fromRunnable(()-> {
            System.out.println("Sending password reset email to: " + to);
            System.out.println("Reset link: " + link);
        }).then();
    }
}
