package com.ctorres.observabilitylab.controller;

import com.ctorres.observabilitylab.dto.LoginRequest;
import com.ctorres.observabilitylab.dto.RegisterRequest;
import com.ctorres.observabilitylab.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
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
    public String logout(@RequestBody String username) throws Exception {
        return service.logout(username);
    }

    @GetMapping("/password/suggestions")
    public List<String> generateSuggestions(@RequestParam int quantity) throws Exception {
        return service.generatePasswordSuggestions(quantity);
    }
}
