package com.ctorres.observabilitylab.dto;

public record LoginResponse(String code, String message, String user, boolean active) {}