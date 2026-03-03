package com.example.mspasswordrecoveryservice.service.impl;

import com.example.mspasswordrecoveryservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Override
    public Mono<Void> send(String to, String link) {
        return Mono.fromRunnable(()-> {
            log.info("Sending password reset email to: {}", to);
            log.info("Reset link: {}", link);
        }).then();
    }
}
