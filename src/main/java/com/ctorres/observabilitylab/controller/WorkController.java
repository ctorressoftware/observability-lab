package com.ctorres.observabilitylab.controller;

import com.ctorres.observabilitylab.metric.WorkMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/work")
public class WorkController {

    private final WorkMetrics metrics;

    public WorkController(WorkMetrics metrics) {
        this.metrics = metrics;
    }

    @GetMapping("/sleep")
    public String sleep(@RequestParam long seconds) {
        return metrics.record("sleep", () -> {
            try {
                Thread.sleep(seconds);
                metrics.incrementRequests("sleep", "success");
                return "Slept time: " + (seconds / 1000) + " seconds.";
            } catch (Exception e) {
                metrics.incrementRequests("sleep", "failed");
                throw e;
            }
        });
    }

    @GetMapping("/cpu")
    public double cpu(@RequestParam int numberOfIterations) {
        return metrics.record("cpu", () -> {
            try {
                double result = 0;
                for (int i = 0; i < numberOfIterations; i++) {
                    metrics.incrementTotalCpuIterations();
                    result += Math.sqrt(i);
                }
                metrics.incrementRequests("cpu", "true");
                return result;

            } catch (Exception e) {
                metrics.incrementRequests("cpu", "failed");
                throw e;
            }
        });
    }

    @GetMapping("/randomFail")
    public String randomFail(@RequestParam float failRate) {
        return metrics.record("randomFail", () -> {
            var random = new Random();
            float number = random.nextFloat();
            if (number < failRate) {
                metrics.incrementRequests("cpu", "failure");
                throw new RuntimeException("controlled error");
            }
            metrics.incrementRequests("randomFail", "success");
            return "success";
        });
    }
}
