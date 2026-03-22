package com.ctorres.observabilitylab.dto;

public class SimulateUserBehaviorResponse {
    private int successfulUserProcess = 0;
    private int totalErrors = 0;
    private int totalRetries = 0;

    public void incrementSuccessfulUserProcess() {
        successfulUserProcess++;
    }

    public void setTotalErrors(int totalErrors) {
        this.totalErrors = totalErrors;
    }

    public void setTotalRetries(int totalRetries) {
        this.totalRetries = totalRetries;
    }

    public int getSuccessfulUserProcess() {
        return successfulUserProcess;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public int getTotalRetries() {
        return totalRetries;
    }
}
