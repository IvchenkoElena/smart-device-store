package ru.yandex.practicum.exception;

public class DeactivateCartException extends RuntimeException {
    public DeactivateCartException(String message) {
        super(message);
    }
}