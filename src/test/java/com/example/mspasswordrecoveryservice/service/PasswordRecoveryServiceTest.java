package com.example.mspasswordrecoveryservice.service;

import com.example.mspasswordrecoveryservice.config.AppConfig;
import com.example.mspasswordrecoveryservice.model.PasswordResetToken;
import com.example.mspasswordrecoveryservice.repository.PasswordResetTokenRepository;
import com.example.mspasswordrecoveryservice.service.impl.EmailServiceImpl;
import com.example.mspasswordrecoveryservice.service.impl.PasswordRecoveryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {

    @Mock
    PasswordResetTokenRepository repository;

    @Mock
    EmailServiceImpl emailService;

    @Mock
    AppConfig config;

    @InjectMocks
    PasswordRecoveryServiceImpl service;

    @Test
    void shouldCreateTokenAndSendEmail() {

        when(repository.save(any()))
                .thenReturn(Mono.just(new PasswordResetToken()));

        when(emailService.send(any(), any()))
                .thenReturn(Mono.empty());

        when(config.getExpiration())
                .thenReturn(15);

        StepVerifier.create(service.requestReset("valdezv231@gmail.com"))
                .verifyComplete();
    }
}