package com.solvd.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Vadym Spitsyn
 * @created 2025-01-02
 */
public class MyRunnableThread implements Runnable {
    private static final Logger logger = LogManager.getLogger(MyRunnableThread.class);

    @Override
    public void run() {
        logger.info("Running thread with implements Runnable: {}", Thread.currentThread().getName());
    }
}
