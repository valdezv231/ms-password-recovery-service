package com.example.mspasswordrecoveryservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmResetDTO {

    @NotBlank(message = "Token is required")
    private String token;
}
