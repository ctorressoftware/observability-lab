package com.ctorres.observabilitylab.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AuthMetrics {
    private final MeterRegistry registry;
    private final AtomicInteger loggedUsers = new AtomicInteger(0);

    public AuthMetrics(MeterRegistry registry) {
        this.registry = registry;
        registry.gauge("logged_users", loggedUsers);
    }

    public void incrementRequests(String endpoint, String result) {
        registry.counter(
                "auth_requests_total",
                "endpoint", endpoint,
                "result", result
        ).increment();
    }

    public <T> T record(String endpoint, Callable<T> callable) throws Exception {

        Timer timer = Timer.builder("auth_duration_seconds")
                .description("Duration of auth endpoints")
                .tag("endpoint", endpoint)
                .publishPercentileHistogram()
                .register(registry);

        return timer.recordCallable(callable);
    }

    public void addLoggedUser() {
        loggedUsers.getAndIncrement();
    }

    public void deleteLoggedUser() {
        if (loggedUsers.get() == 0) throw new RuntimeException("there are no logged users");
        loggedUsers.getAndDecrement();
    }
}
