package com.ctorres.observabilitylab.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WorkMetrics {
    private final MeterRegistry registry;
    private final AtomicInteger loggedUsers = new AtomicInteger(0);

    public WorkMetrics(MeterRegistry registry) {
        this.registry = registry;
        registry.gauge("logged_users", loggedUsers);
    }

    public void incrementRequests(String endpoint, String result) {
        registry.counter(
                "work_requests_total",
                "endpoint", endpoint,
                "result", result
        ).increment();
    }

    public <T> T record(String endpoint, Callable<T> callable) throws Exception {

        Timer timer = Timer.builder("work_duration_seconds")
                .description("Duration of work endpoints")
                .tag("endpoint", endpoint)
                .publishPercentileHistogram()
                .register(registry);

        return timer.recordCallable(callable);
    }

    public void addLoggedUser() {
        loggedUsers.getAndIncrement();
    }

    public void deleteLoggedUser() {
        loggedUsers.getAndDecrement();
    }
}
