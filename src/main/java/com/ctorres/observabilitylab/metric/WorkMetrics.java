package com.ctorres.observabilitylab.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class WorkMetrics {
    private final Counter workRequests;
    private final Timer workDuration;

    public WorkMetrics(MeterRegistry registry) {
        this.workRequests = Counter.builder("work_requests_total")
                .description("Total number of /work requests")
                .register(registry);

        this.workDuration = Timer.builder("work_duration")
                .description("Duration of /work requests")
                .register(registry);
    }

    public void incrementRequests() {
        workRequests.increment();
    }

    public <T> T record(Callable<T> callable) {
        try {
            return workDuration.recordCallable(callable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void record(Runnable runnable) {
        workDuration.record(runnable);
    }
}
