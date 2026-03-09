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
            log.info("Enviando correo de restablecimiento de contraseña a: {}", to);
            log.info("Enlace de restablecimiento: {}", link);
        }).then();
    }
}
