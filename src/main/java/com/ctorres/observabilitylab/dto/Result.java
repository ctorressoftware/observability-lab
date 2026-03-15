package com.ctorres.observabilitylab.dto;

public record Result<T>(boolean success, String errorCode, String errorMessage, T data) {

    public static <T> Result<T> success(T data) {
        return new Result(true, null, null, data);
    }

    public static <T> Result<T> error(String errorCode, String errorMessage) {
        return new Result(false, errorCode, errorMessage, null);
    }
}
