package com.todo.apigateway.exception;

public class EmailNotFoundException extends Exception {
    public EmailNotFoundException() {
        super();
    }

    public EmailNotFoundException(String message) {
        super(message);
    }
}