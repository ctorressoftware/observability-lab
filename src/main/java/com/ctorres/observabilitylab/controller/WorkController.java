package com.ctorres.observabilitylab.controller;

import com.ctorres.observabilitylab.dto.LoginRequest;
import com.ctorres.observabilitylab.service.WorkService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/work")
public class WorkController {
    private final WorkService service;

    public WorkController(WorkService service) {
        this.service = service;
    }

    @GetMapping("/sleep")
    public String sleep(@RequestParam long seconds) throws Exception {
        return service.sleep(seconds);
    }

    @PostMapping("/login")
    public String login(@RequestBody @Validated LoginRequest request) throws Exception {
        return service.login(request);
    }

    @GetMapping("/cpu")
    public String cpu(@RequestParam int numberOfIterations) throws Exception {
        return service.cpu(numberOfIterations);
    }

    @GetMapping("/randomFail")
    public String randomFail() throws Exception {
        return service.randomFail();
    }
}
