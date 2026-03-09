package com.example.mspasswordrecoveryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidationResponse {

    private boolean valid;
}