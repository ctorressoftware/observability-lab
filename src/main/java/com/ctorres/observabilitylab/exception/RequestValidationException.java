package com.ctorres.observabilitylab.exception;

public class RequestValidationException extends RuntimeException {
    public RequestValidationException() {
        super("request validation error");
    }

    public RequestValidationException(String message) {
        super(message);
    }
}
