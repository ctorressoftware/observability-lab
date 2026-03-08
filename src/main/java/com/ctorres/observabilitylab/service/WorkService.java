package com.ctorres.observabilitylab.service;

import com.ctorres.observabilitylab.dto.LoginRequest;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class WorkService {

    private final Random random;

    public WorkService(Random random) {
        this.random = random;
    }

    public boolean login(LoginRequest request) {
        if (request == null) return false;
        if (request.user() == null || request.password() == null) return false;

        boolean result = getArbitraryResult(request, 2);

        if (!result) {
            throw new RuntimeException("login controlled error");
        }

        return true;
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
