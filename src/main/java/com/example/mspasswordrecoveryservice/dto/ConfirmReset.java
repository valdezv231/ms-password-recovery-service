package com.example.mspasswordrecoveryservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmReset {

    @NotBlank(message = "Token es obligatorio")
    private String token;
}
