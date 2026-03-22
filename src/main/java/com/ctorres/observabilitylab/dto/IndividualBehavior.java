package com.ctorres.observabilitylab.dto;

public class IndividualBehavior {
    private int errors = 0;
    private int retries = 0;

    public void incrementErrors() {
        errors++;
    }

    public int getErrors() {
        return errors;
    }

    public void incrementRetries() {
        retries++;
    }

    public int getRetries() {
        return retries;
    }
}