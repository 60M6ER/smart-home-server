package ru.larionov.backend.exception;

public class UserNotFound extends RuntimeException{
    public UserNotFound(String message) {
        super("User not found with " + message);
    }
}
