package com.ctorres.observabilitylab.service;

import com.ctorres.observabilitylab.dto.LoginRequest;
import com.ctorres.observabilitylab.dto.RegisterRequest;
import com.ctorres.observabilitylab.exception.ControlledErrorException;
import com.ctorres.observabilitylab.exception.InterruptedThreadException;
import com.ctorres.observabilitylab.exception.RequestValidationException;
import com.ctorres.observabilitylab.helper.FutureHelper;
import com.ctorres.observabilitylab.metric.AuthMetrics;
import com.ctorres.observabilitylab.service.password_generator.PasswordSuggestionWorker;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class AuthService {
    private final Random random;
    private final AuthMetrics metrics;
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    public AuthService(Random random, AuthMetrics metrics) {
        this.random = random;
        this.metrics = metrics;
    }

    public String register(RegisterRequest request) throws Exception {
        return metrics.record("register", () -> {
            if (request == null) {
                metrics.incrementRequests("register", "failed");
                throw new RequestValidationException();
            }
            if (request.user() == null || request.password() == null) {
                metrics.incrementRequests("register", "failed");
                throw new RequestValidationException("user and password are required");
            }

            boolean result = getArbitraryResult(request, 3);
            metrics.incrementRequests("register", result ? "success" : "failed");
            if (!result) throw new ControlledErrorException("register controlled error");
            return "user registered correctly.";
        });
    }

    public String login(LoginRequest request) throws Exception {
        return metrics.record("login", () -> {
            if (request == null) {
                metrics.incrementRequests("login", "failed");
                throw new RequestValidationException();
            }
            if (request.user() == null || request.password() == null) {
                metrics.incrementRequests("login", "failed");
                throw new RequestValidationException("user and password are required");
            }

            boolean result = getArbitraryResult(request, 2);
            metrics.incrementRequests("login", result ? "success" : "failed");

            if (!result) throw new ControlledErrorException("login controlled error");
            metrics.addLoggedUser();

            return "login succeeded";
        });
    }

    public String logout(String username) throws Exception {
        return metrics.record("logout", () -> {

            if (username == null) {
                metrics.incrementRequests("logout", "failed");
                throw new RequestValidationException("username required");
            }

            boolean result = getArbitraryResult(username, 1);
            metrics.incrementRequests("logout", result ? "success" : "failed");

            if (!result) throw new ControlledErrorException("logout controlled error");
            metrics.deleteLoggedUser();

            return "logout succeeded";
        });
    }

    public List<String> generatePasswordSuggestions(int quantity) throws Exception {

        return metrics.record("password_suggestions", () -> {

            if (quantity <= 0) {
                metrics.incrementRequests("password_suggestions", "failed");
                throw new RequestValidationException("quantity must to be higher than zero");
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
                throw new InterruptedThreadException(e);
            }
        });
    }

    private boolean getArbitraryResult(Object object, long seconds) {
        try {
            LOGGER.info("Processed object: " + object);
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedThreadException(e);
        }
        return random.nextFloat() < 0.5; // 50% OK - 50% Throw an RuntimeException
    }
}
