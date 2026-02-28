package com.example.mspasswordrecoveryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleTokenNotFound(
            TokenNotFoundException ex,
            ServerWebExchange exchange){

        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidToken(
            InvalidTokenException ex,
            ServerWebExchange exchange){

        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGenericException(
            Exception ex,
            ServerWebExchange exchange){

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                exchange.getRequest().getPath().value()
        );
    }

    private ApiError buildError(
            HttpStatus status,
            String message,
            String path
    ){
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(message)
                .path(path)
                .build();
    }
}