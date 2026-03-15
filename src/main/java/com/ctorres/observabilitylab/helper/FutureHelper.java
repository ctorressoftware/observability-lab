package com.ctorres.observabilitylab.helper;

import com.ctorres.observabilitylab.exception.InterruptedThreadException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureHelper<T> {
    public static <T> T getCheckedException(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedThreadException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
