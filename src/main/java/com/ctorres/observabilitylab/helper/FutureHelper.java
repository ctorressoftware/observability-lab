package com.ctorres.observabilitylab.helper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureHelper<T> {
    public static <T> T getCheckedException(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
