package com.example.taskmanagement.exception;

public class InvalidTaskStatusException extends RuntimeException {

    public InvalidTaskStatusException(String message) {
        super(message);
    }
}
