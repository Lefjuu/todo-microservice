package com.todo.apigateway.exception;

public class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException() {
        super();
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

