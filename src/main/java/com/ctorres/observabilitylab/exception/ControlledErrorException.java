package com.ctorres.observabilitylab.exception;

public class ControlledErrorException extends RuntimeException {
    public ControlledErrorException(String message) {
        super(message);
    }
}
