package com.example.mspasswordrecoveryservice.exception;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
}
