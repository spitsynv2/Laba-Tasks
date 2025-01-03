package com.solvd.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
  * @author Vadym Spitsyn
  * @created 2025-01-02
*/
public class MyThread extends Thread {
    private static final Logger logger = LogManager.getLogger(MyThread.class);

    @Override
    public void run() {
        logger.info("Running thread with extends Thread: {}", Thread.currentThread().getName());
    }
}
