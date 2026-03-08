package com.ctorres.observabilitylab.controller;

import com.ctorres.observabilitylab.dto.LoginRequest;
import com.ctorres.observabilitylab.metric.WorkMetrics;
import com.ctorres.observabilitylab.service.WorkService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/work")
public class WorkController {
    private final WorkMetrics metrics;
    private final WorkService service;

    public WorkController(WorkMetrics metrics, WorkService service) {
        this.metrics = metrics;
        this.service = service;
    }

    @GetMapping("/sleep")
    public String sleep(@RequestParam long seconds) throws Exception {
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

    @PostMapping("/login")
    public String login(@RequestBody @Validated LoginRequest request) throws Exception {
        return metrics.record("login", () ->
                service.login(request) ? "login succeeded" : "login failed"
        );
    }

    @GetMapping("/cpu")
    public double cpu(@RequestParam int numberOfIterations) throws Exception {
        return metrics.record("cpu", () -> {
            try {
                double result = 0;
                for (int i = 0; i < numberOfIterations; i++) {
                    result += Math.sqrt(i);
                }
                metrics.incrementRequests("cpu", "success");
                return result;

            } catch (Exception e) {
                metrics.incrementRequests("cpu", "failed");
                throw e;
            }
        });
    }

    @GetMapping("/randomFail")
    public String randomFail(@RequestParam float failRate) throws Exception {
        return metrics.record("randomFail", () -> {
            var random = new Random();
            float number = random.nextFloat();
            if (number < failRate) {
                metrics.incrementRequests("randomFail", "failed");
                throw new RuntimeException("controlled error");
            }
            metrics.incrementRequests("randomFail", "success");
            return "success";
        });
    }
}
