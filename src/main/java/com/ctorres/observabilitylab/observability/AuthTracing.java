package com.ctorres.observabilitylab.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class AuthTracing {

    private final ObservationRegistry observationRegistry;

    public AuthTracing(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public <T> T inSpan(String spanName, Callable<T> action) throws Exception {
        Observation observation = Observation.createNotStarted(spanName, observationRegistry);

        observation.start();
        try (Observation.Scope scope = observation.openScope()) {
            return action.call();
        } catch (Exception e) {
            observation.error(e);
            throw e;
        } finally {
            observation.stop();
        }
    }

    public <T> T inSpan(String spanName, String endpoint, Callable<T> action) throws Exception {
        Observation observation = Observation.createNotStarted(spanName, observationRegistry)
                .lowCardinalityKeyValue("endpoint", endpoint);

        observation.start();
        try (Observation.Scope scope = observation.openScope()) {
            return action.call();
        } catch (Exception e) {
            observation.error(e);
            throw e;
        } finally {
            observation.stop();
        }
    }
}