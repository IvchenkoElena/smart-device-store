package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ProductNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(RuntimeException e) {
        log.warn("404 - NOT_FOUND");
        return new ApiError(HttpStatus.NOT_FOUND, "Product not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(RuntimeException e) {
        log.warn("500 - INTERNAL_SERVER_ERROR");
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e.getMessage());
    }
}
