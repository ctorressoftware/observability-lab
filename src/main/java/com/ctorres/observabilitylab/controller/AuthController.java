package com.ctorres.observabilitylab.controller;

import com.ctorres.observabilitylab.dto.LoginRequest;
import com.ctorres.observabilitylab.dto.RegisterRequest;
import com.ctorres.observabilitylab.service.WorkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work")
public class WorkController {
    private final WorkService service;

    public WorkController(WorkService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) throws Exception {
        return service.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) throws Exception {
        return service.login(request);
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String username) throws Exception {
        return service.logout(username);
    }

    @GetMapping("/password/suggestions")
    public List<String> generateSuggestions(@RequestParam int size) throws Exception {
        return service.generatePasswordSuggestions(size);
    }
}
