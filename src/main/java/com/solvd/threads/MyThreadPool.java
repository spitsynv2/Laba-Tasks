package com.solvd.threads;

import com.solvd.threads.entity.MyThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vadym Spitsyn
 * @created 2025-01-02
 */
public class MyThreadPool {
    private static final Logger logger = LogManager.getLogger(MyThread.class);
    private static MyThreadPool instance;
    private final List<Thread> threads;
    private int maxSize = 0;
    private boolean isStopped = false;

    private MyThreadPool(int size) {
        threads = new ArrayList<>(size);
        maxSize = size;
    }

    public static MyThreadPool getInstance(int size) {
        if (instance == null) {
            instance = new MyThreadPool(size);
        }
        return instance;
    }

    public void addThread(Thread thread) {
        if (isStopped) {
            logger.error("ThreadPool is stopped/stopping");
            return;
        }

        logger.info("Current Thread Pool size: {}/{}", threads.size(), maxSize);
        while (maxSize==threads.size()){
            threads.removeIf(x->!x.isAlive());
        }

        logger.info("Added: {} to thread pool", thread.getName());
        threads.add(thread);
        thread.start();
    }

    public void safeStop(){
        isStopped = true;
    }
}
