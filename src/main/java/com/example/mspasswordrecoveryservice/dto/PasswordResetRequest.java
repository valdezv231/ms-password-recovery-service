package com.example.mspasswordrecoveryservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {

    @NotBlank(message = "Email es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    private String email;
}
