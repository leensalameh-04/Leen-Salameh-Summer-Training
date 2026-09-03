package com.example.taskmanagement.exception;

public class DuplicateProjectNameException extends RuntimeException {

    public DuplicateProjectNameException(String name) {
        super("A project with the name '" + name + "' already exists");
    }
}
