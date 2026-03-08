package com.ctorres.observabilitylab.service;

import com.ctorres.observabilitylab.dto.LoginRequest;
import com.ctorres.observabilitylab.metric.WorkMetrics;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class WorkService {

    private final Random random;
    private final WorkMetrics metrics;

    public WorkService(Random random, WorkMetrics metrics) {
        this.random = random;
        this.metrics = metrics;
    }

    public String sleep(long seconds) throws Exception {
        return metrics.record("sleep", () -> {
            try {
                Thread.sleep(seconds * 1000);
                metrics.incrementRequests("sleep", "success");
                return "Slept time: " + seconds + " seconds.";
            } catch (Exception e) {
                metrics.incrementRequests("sleep", "failed");
                throw e;
            }
        });
    }

    public String login(LoginRequest request) throws Exception {
        return metrics.record("login", () -> {
            if (request == null) return "validation error";
            if (request.user() == null || request.password() == null) return "validation error";

            boolean result = getArbitraryResult(request, 2);

            if (!result) throw new RuntimeException("login controlled error");

            return  "login succeeded";
        });
    }

    public String cpu(int numberOfIterations) throws Exception {
        return metrics.record("cpu", () -> {
            try {
                double result = 0;
                for (int i = 0; i < numberOfIterations; i++) {
                    result += Math.sqrt(i);
                }
                metrics.incrementRequests("cpu", "success");
                return "result: " + result;

            } catch (Exception e) {
                metrics.incrementRequests("cpu", "failed");
                throw e;
            }
        });
    }

    public String randomFail() throws Exception {
        return metrics.record("randomFail", () -> {
            var result = getArbitraryResult(new Object(), 2000);

            if (!result) {
                metrics.incrementRequests("randomFail", "failed");
                throw new RuntimeException("controlled error");
            }

            metrics.incrementRequests("randomFail", "success");
            return "success";
        });
    }

    private boolean getArbitraryResult(Object object, long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return random.nextFloat() < 0.5;
    }

}
