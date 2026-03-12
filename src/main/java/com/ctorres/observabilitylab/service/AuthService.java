package com.ctorres.observabilitylab.service;

import com.ctorres.observabilitylab.dto.LoginRequest;
import com.ctorres.observabilitylab.dto.RegisterRequest;
import com.ctorres.observabilitylab.helper.FutureHelper;
import com.ctorres.observabilitylab.metric.AuthMetrics;
import com.ctorres.observabilitylab.service.password_generator.PasswordSuggestionWorker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AuthService {
    private final Random random;
    private final AuthMetrics metrics;

    public AuthService(Random random, AuthMetrics metrics) {
        this.random = random;
        this.metrics = metrics;
    }

    public String register(RegisterRequest request) throws Exception {
        return metrics.record("register", () -> {
            if (request == null) {
                metrics.incrementRequests("register", "failed");
                throw new RuntimeException("request validation error");
            }
            if (request.user() == null || request.password() == null) {
                metrics.incrementRequests("register", "failed");
                throw new RuntimeException("user and password are required");
            }

            boolean result = getArbitraryResult(request, 3);
            metrics.incrementRequests("register", result ? "success" : "failed");
            if (!result) throw new RuntimeException("register controlled error");
            return "user registered correctly.";

        });
    }

    public String login(LoginRequest request) throws Exception {
        return metrics.record("login", () -> {
            if (request == null) {
                metrics.incrementRequests("login", "failed");
                throw new RuntimeException("request validation error");
            }
            if (request.user() == null || request.password() == null) {
                metrics.incrementRequests("login", "failed");
                throw new RuntimeException("user and password are required");
            }

            boolean result = getArbitraryResult(request, 2);
            metrics.incrementRequests("login", result ? "success" : "failed");

            if (!result) throw new RuntimeException("login controlled error");
            metrics.addLoggedUser();

            return "login succeeded";
        });
    }

    public String logout(String username) throws Exception {
        return metrics.record("logout", () -> {

            if (username == null) {
                metrics.incrementRequests("logout", "failed");
                return "username required";
            }

            boolean result = getArbitraryResult(username, 1);
            metrics.incrementRequests("logout", result ? "success" : "failed");

            if (!result) throw new RuntimeException("logout controlled error");
            metrics.deleteLoggedUser();

            return "logout succeeded";
        });
    }

    public List<String> generatePasswordSuggestions(int quantity) throws Exception {

        return metrics.record("password_suggestions", () -> {

            if (quantity <= 0) {
                metrics.incrementRequests("password_suggestions", "failed");
                throw new RuntimeException("quantity must to be higher than zero");
            }

            var suggestionWorkers = new ArrayList<PasswordSuggestionWorker>(quantity);

            for (int i = 0; i < quantity; i++)
                suggestionWorkers.add(new PasswordSuggestionWorker());

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

                var futures = executor.invokeAll(suggestionWorkers);
                List<String> suggestions = futures.stream()
                        .map(FutureHelper::getCheckedException)
                        .toList();

                boolean isGenerated = suggestions.size() == quantity;
                metrics.incrementRequests("password_suggestions", isGenerated ? "success" : "failed");

                return suggestions;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
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
