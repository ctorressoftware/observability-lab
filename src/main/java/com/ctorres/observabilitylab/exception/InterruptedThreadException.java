package com.ctorres.observabilitylab.exception;

public class InterruptedThreadException extends RuntimeException {
    public InterruptedThreadException(InterruptedException ex) {
        super(ex.getCause());
    }
}
