package com.ctorres.observabilitylab.controller;

import com.ctorres.observabilitylab.metric.WorkMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
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
        try {
            metrics.incrementRequests();
            Thread.sleep(seconds);
            return "I slept like " + (seconds/1000) + " seconds.";
        } catch (InterruptedException e) {
            Thread.interrupted();
            throw new RuntimeException("Thread " + Thread.currentThread().getName()
                    + " was interrupted", e);
        }
    }

    @GetMapping("/cpu")
    public List<String> cpu(@RequestParam int numberOfIterations) {
        metrics.incrementRequests();
        var iterations = new ArrayList<String>();
        for (int i = 0; i < numberOfIterations; i++) {
           iterations.add("Iteration " + (i + 1));
        }
        return iterations;
    }

    @GetMapping("/randomFail")
    public String randomFail(@RequestParam float failRate) {
        metrics.incrementRequests();
        var random = new Random();
        float number = random.nextFloat();
        if (number < failRate) {
            throw new RuntimeException("It has occurred an controlled error");
        }
        return "success!";
    }
}
