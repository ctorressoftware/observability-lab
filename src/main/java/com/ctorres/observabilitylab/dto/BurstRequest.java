package com.ctorres.observabilitylab.dto;

public record BurstRequest(String endpoint, int times, int parallelism) {}
