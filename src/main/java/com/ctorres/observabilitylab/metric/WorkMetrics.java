package com.ctorres.observabilitylab.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class WorkMetrics {
    private final Counter workRequests;

    public WorkMetrics(MeterRegistry registry) {
        this.workRequests = Counter.builder("work_requests_total")
                .description("Total number of /work requests")
                .register(registry);
    }

    public void incrementRequests() {
        workRequests.increment();
    }
}
