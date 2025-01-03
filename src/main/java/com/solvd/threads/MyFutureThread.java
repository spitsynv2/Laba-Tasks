package com.solvd.threads;

import java.util.concurrent.*;

/**
 * @author Vadym Spitsyn
 * @created 2025-01-02
 */
public class MyFutureThread implements Callable<String> {

    @Override
    public String call() throws Exception {
        return "Running thread with implements Callable (Future): "+ Thread.currentThread().getName();
    }
}
