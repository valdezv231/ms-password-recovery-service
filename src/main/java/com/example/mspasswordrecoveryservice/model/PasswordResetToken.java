package com.example.mspasswordrecoveryservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("password_reset_tokens")
public class PasswordResetToken {

    @Id
    private UUID id;

    private String email;
    private String token;
    private LocalDateTime expiration;
    private Boolean used;
}
