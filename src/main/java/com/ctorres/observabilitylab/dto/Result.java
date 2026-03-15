package com.ctorres.observabilitylab.dto;

public class Result<T> {
    private final boolean success;
    private final String errorCode;
    private final String errorMessage;
    private final T data;

    public Result (boolean success, String errorCode, String errorMessage, T data) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result(true, null, null, data);
    }

    public static <T> Result<T> error(String errorCode, String errorMessage) {
        return new Result(false, errorCode, errorMessage, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getData() {
        return data;
    }
}
