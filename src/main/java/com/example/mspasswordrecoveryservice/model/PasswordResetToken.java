package com.example.mspasswordrecoveryservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("password_reset_tokens")
public class PasswordResetToken {

    @Id
    private UUID id;

    private String email;
    private String token;
    private LocalDateTime expiration;
    private Boolean used;
}
