package com.ctorres.observabilitylab.service;

import com.ctorres.observabilitylab.dto.*;
import com.ctorres.observabilitylab.exception.ControlledErrorException;
import com.ctorres.observabilitylab.exception.InterruptedThreadException;
import com.ctorres.observabilitylab.exception.RequestValidationException;
import com.ctorres.observabilitylab.helper.FutureHelper;
import com.ctorres.observabilitylab.metric.AuthMetrics;
import com.ctorres.observabilitylab.service.password_generator.PasswordSuggestionWorker;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final Random random;
    private final AuthMetrics metrics;
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    public AuthService(Random random, AuthMetrics metrics) {
        this.random = random;
        this.metrics = metrics;
    }

    public RegisterResponse register(RegisterRequest request) throws Exception {
        return metrics.record("register", () -> {
            if (request == null) {
                metrics.incrementRequests("register", "failed");
                throw new RequestValidationException();
            }
            if (request.user() == null || request.password() == null) {
                metrics.incrementRequests("register", "failed");
                throw new RequestValidationException("user and password are required");
            }

            boolean result = simulateAuthProcessing(request, random.nextInt(5));
            metrics.incrementRequests("register", result ? "success" : "failed");
            if (!result) throw new ControlledErrorException("register controlled error");

            return new RegisterResponse(
                    "USER_REGISTERED",
                    "user registered correctly.",
                    request.user(),
                    true);
        });
    }

    public LoginResponse login(LoginRequest request) throws Exception {
        return metrics.record("login", () -> {
            if (request == null) {
                metrics.incrementRequests("login", "failed");
                throw new RequestValidationException();
            }
            if (request.user() == null || request.password() == null) {
                metrics.incrementRequests("login", "failed");
                throw new RequestValidationException("user and password are required");
            }

            boolean result = simulateAuthProcessing(request, 2);
            metrics.incrementRequests("login", result ? "success" : "failed");

            if (!result) throw new ControlledErrorException("login controlled error");
            metrics.addLoggedUser();

            return new LoginResponse(
                    "USER_LOGGED",
                    "login succeeded",
                    request.user(),
                    true
            );
        });
    }

    public LogoutResponse logout(String username) throws Exception {
        return metrics.record("logout", () -> {

            if (username == null) {
                metrics.incrementRequests("logout", "failed");
                throw new RequestValidationException("username required");
            }

            boolean result = simulateAuthProcessing(username, 1);
            metrics.incrementRequests("logout", result ? "success" : "failed");

            if (!result) throw new ControlledErrorException("logout controlled error");
            metrics.deleteLoggedUser();

            return new LogoutResponse(
                    "USED_LOGGED_OUT",
                    username + " logged out successfully"
            );
        });
    }

    public SuggestionsResponse generatePasswordSuggestions(int quantity) throws Exception {

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
                Set<String> suggestions = futures.stream()
                        .map(FutureHelper::getCheckedException)
                        .collect(Collectors.toSet());

                boolean isGenerated = suggestions.size() == quantity;
                metrics.incrementRequests("password_suggestions", isGenerated ? "success" : "failed");

                return new SuggestionsResponse(
                        "PASSWORD_SUGGESTIONS_GENERATED",
                        suggestions
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedThreadException(e);
            }
        });
    }

    public BurstResponse burst(BurstRequest request) throws Exception {
        return metrics.record("burst", () -> {

            if (request == null) {
                metrics.incrementRequests("burst", "failed");
                throw new RequestValidationException("valid endpoint is required");
            }

            if (request.endpoint().isBlank() || request.times() == 0 || request.parallelism() == 0) {
                metrics.incrementRequests("burst", "failed");
                throw new RequestValidationException();
            }

            try (var executor = Executors.newFixedThreadPool(request.parallelism())) {
                var callables = getCallables(request.endpoint(), request.times());
                var futures = executor.invokeAll(callables);
                var responses = futures.stream()
                        .map(FutureHelper::getCheckedException)
                        .toList();
                boolean result = responses.size() == request.times();
                metrics.incrementRequests("burst", result ? "success" : "failed");

                if (!result) throw new ControlledErrorException("burst controlled error");
                return new BurstResponse(
                        "TASKS_COMPLETED",
                        request.times() + " tasks completed successfully"
                );
            }
        });
    }

    private @NonNull ArrayList<Callable<Object>> getCallables(String endpoint, int times) {
        var callables = new ArrayList<Callable<Object>>();
        for (int i = 0; i < times; i++) {
            callables.add(() -> switch (endpoint) {
                case "register" -> register(new RegisterRequest("admin", "password"));
                case "login" -> login(new LoginRequest("admin", "password"));
                case "logout" -> logout("admin");
                case "password_suggestions" -> generatePasswordSuggestions(10);
                default -> null;
            });
        }
        return callables;
    }

    private void simulateUserBehavior(SimulateUserBehaviorRequest request) {

        // TODO: implement user's behavior simulation
        try (var executor = Executors.newFixedThreadPool(request.parallelism())) {

        }

    }

    private @NonNull ArrayList<Callable<String>> getBehaviorCallable(int times) {
        var callables = new ArrayList<Callable<String>>();
        for (int i = 0; i < times; i++) {
            final int index = i;
            callables.add(() -> {


                return "";
            });
        }
        return callables;
    }

    private void userBehavior(int index) throws Exception {
        Set<String> suggestions = generatePasswordSuggestions(random.nextInt(20)).suggestions();

        String user = "user_" + index;
        String password = "example_password"; //suggestions.(random.nextInt(20));

        register(new RegisterRequest(user, password ));
    }

    private boolean simulateAuthProcessing(Object object, long seconds) {
        try {
            LOGGER.info("Processed object: " + object);
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedThreadException(e);
        }
        return random.nextFloat() < 0.9; // 90% OK - 10% Throw an RuntimeException
    }
}
