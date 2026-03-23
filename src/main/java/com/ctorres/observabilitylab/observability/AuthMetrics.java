package com.ctorres.observabilitylab.observability;

import com.ctorres.observabilitylab.exception.ControlledErrorException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AuthMetrics {
    private final MeterRegistry registry;
    private final AtomicInteger loggedUsers = new AtomicInteger(0);
    private final Map<String, Timer> timersByEndpoint = new ConcurrentHashMap<>();

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

    private Timer buildTimer(String endpoint) {
        return Timer.builder("auth_duration_seconds")
                .description("Duration of auth endpoints")
                .tag("endpoint", endpoint)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .register(registry);
    }

    public <T> T record(String endpoint, Callable<T> callable) throws Exception {
        Timer timer = timersByEndpoint.computeIfAbsent(endpoint, this::buildTimer);
        return timer.recordCallable(callable);
    }

    public void addLoggedUser() {
        loggedUsers.getAndIncrement();
    }

    public void deleteLoggedUser() {
        if (loggedUsers.get() == 0) throw new ControlledErrorException("there are no logged users");
        loggedUsers.getAndDecrement();
    }
}