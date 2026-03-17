package com.ctorres.observabilitylab.controller;

import com.ctorres.observabilitylab.dto.*;
import com.ctorres.observabilitylab.service.AuthService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Result<RegisterResponse>> register(@RequestBody RegisterRequest request) throws Exception {
        return ResponseEntity.ok(Result.success(service.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<Result<String>> login(@RequestBody LoginRequest request) throws Exception {
        return ResponseEntity.ok(Result.success(service.login(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Result<String>> logout(@RequestBody String username) throws Exception {
        return ResponseEntity.ok(Result.success(service.logout(username)));
    }

    @GetMapping("/password/suggestions")
    public ResponseEntity<Result<List<String>>> generateSuggestions(@RequestParam int quantity) throws Exception {
        return ResponseEntity.ok(Result.success(service.generatePasswordSuggestions(quantity)));
    }

    @PostMapping("/burst")
    public ResponseEntity<Result<String>> burst(@RequestBody BurstRequest request) throws Exception {
        return ResponseEntity.ok(Result.success(service.burst(request)));
    }
}
