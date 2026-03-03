package com.example.mspasswordrecoveryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleTokenNotFound(
            TokenNotFoundException ex,
            ServerWebExchange exchange){

        log.warn("Token not found — path: {}, message: {}",
                exchange.getRequest().getPath().value(), ex.getMessage());
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

        log.warn("Invalid token attempt — path: {}, message: {}",
                exchange.getRequest().getPath().value(), ex.getMessage());
        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(ServerWebInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleWebInputException(
            ServerWebInputException ex,
            ServerWebExchange exchange) {

        log.warn("Bad request on path: {} — {}",
                exchange.getRequest().getPath().value(), ex.getReason());
        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getReason(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Unexpected error on path: {} — {}",
                exchange.getRequest().getPath().value(), ex.getMessage(), ex);
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