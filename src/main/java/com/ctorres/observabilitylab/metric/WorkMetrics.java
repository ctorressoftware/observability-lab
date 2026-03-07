package com.ctorres.observabilitylab.metric;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WorkMetrics {
    private final MeterRegistry registry;
    private final AtomicInteger totalCpuIterations = new AtomicInteger(0);

    public WorkMetrics(MeterRegistry registry) {
        this.registry = registry;
        registry.gauge("work_cpu_iterations", totalCpuIterations);
    }

    public void incrementRequests(String endpoint, String result) {
        registry.counter(
                "work_requests_total",
                "endpoint", endpoint,
                "result", result
        ).increment();
    }

    public <T> T record(String endpoint, Callable<T> callable) {
        try {
            return registry.timer("work_duration", "endpoint", endpoint)
                    .recordCallable(callable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void incrementTotalCpuIterations() {
        totalCpuIterations.getAndIncrement();
    }
}
