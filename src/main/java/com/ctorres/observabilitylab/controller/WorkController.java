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
            Thread.sleep(seconds);
            metrics.incrementRequests("sleep", "success");
            return "I slept like " + (seconds / 1000) + " seconds.";
        });
    }

    @GetMapping("/cpu")
    public double cpu(@RequestParam int numberOfIterations) {
        return metrics.record("cpu", () -> {
            double result = 0;
            for (int i = 0; i < numberOfIterations; i++) {
                metrics.incrementTotalCpuIterations();
                result += Math.sqrt(i);
            }
            metrics.incrementRequests("cpu", "true");
            return result;
        });
    }

    @GetMapping("/randomFail")
    public String randomFail(@RequestParam float failRate) {
        return metrics.record("randomFail", () -> {
            var random = new Random();
            float number = random.nextFloat();
            if (number < failRate) {
                metrics.incrementRequests("cpu", "failure");
                throw new RuntimeException("It has occurred an controlled error");
            }
            metrics.incrementRequests("cpu", "success");
            return "success";
        });
    }
}
