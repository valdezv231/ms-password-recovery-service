package com.example.mspasswordrecoveryservice.service;

import reactor.core.publisher.Mono;

public interface EmailService {

    Mono<Void> send(String to,String link);
}
