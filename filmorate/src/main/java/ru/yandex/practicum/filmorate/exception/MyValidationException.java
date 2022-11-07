package ru.yandex.practicum.filmorate.exception;

public class MyValidationException extends Throwable {
    public MyValidationException(final String message) {
        super(message);
    }
}