package com.example.mspasswordrecoveryservice.service;

import com.example.mspasswordrecoveryservice.config.AppConfig;
import com.example.mspasswordrecoveryservice.dto.ConfirmReset;
import com.example.mspasswordrecoveryservice.dto.PasswordResetRequest;
import com.example.mspasswordrecoveryservice.exception.InvalidTokenException;
import com.example.mspasswordrecoveryservice.exception.TokenNotFoundException;
import com.example.mspasswordrecoveryservice.model.PasswordResetToken;
import com.example.mspasswordrecoveryservice.repository.PasswordResetTokenRepository;
import com.example.mspasswordrecoveryservice.service.impl.PasswordRecoveryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {

    @Mock
    PasswordResetTokenRepository repository;

    @Mock
    EmailService emailService;

    @Mock
    AppConfig appConfig;

    @InjectMocks
    PasswordRecoveryServiceImpl service;

    // ─── helpers ────────────────────────────────────────────────────────────

    private PasswordResetToken validToken() {
        return PasswordResetToken.builder()
                .token("valid-token")
                .email("user@example.com")
                .expiration(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
    }

    private PasswordResetToken expiredToken() {
        return PasswordResetToken.builder()
                .token("expired-token")
                .email("user@example.com")
                .expiration(LocalDateTime.now().minusMinutes(5))
                .used(false)
                .build();
    }

    private PasswordResetToken usedToken() {
        return PasswordResetToken.builder()
                .token("used-token")
                .email("user@example.com")
                .expiration(LocalDateTime.now().plusMinutes(10))
                .used(true)
                .build();
    }

    // ─── requestReset ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("requestReset")
    class RequestReset {

        @BeforeEach
        void setup() {
            when(appConfig.getExpiration()).thenReturn(10);
        }

        @Test
        @DisplayName("should create token and send email → completes")
        void success() {
            PasswordResetRequest dto = new PasswordResetRequest();
            dto.setEmail("user@example.com");

            when(repository.save(any())).thenReturn(Mono.just(validToken()));
            when(emailService.send(any(), any())).thenReturn(Mono.empty());

            StepVerifier.create(service.requestReset(dto))
                    .verifyComplete();

            verify(repository).save(any());
            verify(emailService).send(eq("user@example.com"), any());
        }

        @Test
        @DisplayName("should propagate error when email send fails")
        void emailSendFails() {
            PasswordResetRequest dto = new PasswordResetRequest();
            dto.setEmail("user@example.com");

            when(repository.save(any())).thenReturn(Mono.just(validToken()));
            when(emailService.send(any(), any()))
                    .thenReturn(Mono.error(new RuntimeException("SMTP error")));

            StepVerifier.create(service.requestReset(dto))
                    .expectErrorMessage("SMTP error")
                    .verify();
        }
    }

    // ─── validateToken ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("validateToken")
    class ValidateToken {

        @Test
        @DisplayName("valid token → returns valid=true")
        void validToken() {
            when(repository.findByToken("valid-token"))
                    .thenReturn(Mono.just(PasswordRecoveryServiceTest.this.validToken()));

            StepVerifier.create(service.validateToken("valid-token"))
                    .assertNext(r -> assertThat(r.isValid()).isTrue())
                    .verifyComplete();
        }

        @Test
        @DisplayName("token not found → throws TokenNotFoundException")
        void tokenNotFound() {
            when(repository.findByToken("ghost")).thenReturn(Mono.empty());

            StepVerifier.create(service.validateToken("ghost"))
                    .expectError(TokenNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("already used token → throws InvalidTokenException")
        void alreadyUsedToken() {
            when(repository.findByToken("used-token"))
                    .thenReturn(Mono.just(usedToken()));

            StepVerifier.create(service.validateToken("used-token"))
                    .expectError(InvalidTokenException.class)
                    .verify();
        }
    }

    // ─── markAsUsed ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("markAsUsed")
    class MarkAsUsed {

        @Test
        @DisplayName("existing token → sets used=true and completes")
        void success() {
            ConfirmReset dto = new ConfirmReset();
            dto.setToken("valid-token");

            PasswordResetToken token = validToken();
            when(repository.findByToken("valid-token")).thenReturn(Mono.just(token));
            when(repository.save(any())).thenReturn(Mono.just(token));

            StepVerifier.create(service.markAsUsed(dto))
                    .verifyComplete();

            verify(repository).save(argThat(t -> t.getUsed().equals(true)));
        }

        @Test
        @DisplayName("token not found → throws TokenNotFoundException")
        void tokenNotFound() {
            ConfirmReset dto = new ConfirmReset();
            dto.setToken("ghost");

            when(repository.findByToken("ghost")).thenReturn(Mono.empty());

            StepVerifier.create(service.markAsUsed(dto))
                    .expectError(TokenNotFoundException.class)
                    .verify();
        }
    }
}