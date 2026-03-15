package com.ctorres.observabilitylab.exception;

import java.util.concurrent.ExecutionException;

public class ExecutionThreadException extends RuntimeException {
    public ExecutionThreadException(ExecutionException ex) {
        super(ex);
    }
}
