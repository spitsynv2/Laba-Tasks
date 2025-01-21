package com.solvd.threads;

import com.solvd.threads.entity.MyFutureThread;
import com.solvd.threads.entity.MyRunnableThread;
import com.solvd.threads.entity.MyThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Vadym Spitsyn
 * @created 2025-01-02
 */
public class Runner {
    private static final Logger logger = LogManager.getLogger(Runner.class);

    public static void main(String[] args) {
        //runThreads();
        //deadLock();
        threadPoolTest();
    }

    public static void runThreads(){
        ExecutorService executor = Executors.newFixedThreadPool(3);

        MyThread myThread = new MyThread();
        Future<String> future = executor.submit(new MyFutureThread());

        executor.submit(myThread);
        executor.submit(new MyRunnableThread());

        try {
            logger.info(future.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    public static void deadLock(){
        Object lock1 = new Object();
        Object  lock2 = new Object();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Thread thread1 = new Thread(()->{
            logger.info("Running thread1 {}", Thread.currentThread().getName());

            synchronized (lock1){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (lock2){

                }
            }

            logger.info("Finishing thread1 {}", Thread.currentThread().getName());

        }, "Thread1");

        Thread thread2 = new Thread(()->{
            logger.info("Running thread2 {}", Thread.currentThread().getName());

            synchronized (lock2){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (lock1){

                }
            }
            
            logger.info("Finishing thread2 {}", Thread.currentThread().getName());

        }, "Thread2");

        executor.execute(thread1);
        executor.execute(thread2);
    }

    public static void threadPoolTest(){
        MyConnectionPool.getInstance();

        Thread thread1 = new Thread(()->{
            try {
                Connection connection = MyConnectionPool.getConnection();
                logger.info("Connection acquired from pool! Thread1");

                Statement stmt = connection.createStatement();
                stmt.execute("SELECT id from test");

                Thread.sleep(5000);
                MyConnectionPool.releaseConnection(connection);
                logger.info("Connection returned to pool. Thread1");
            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            }
            logger.info(Thread.currentThread().getName() + " Finished work");
        }, "Thread1");

        Thread thread2 = new Thread(()->{
            try {
                Connection connection = MyConnectionPool.getConnection();
                logger.info("Connection acquired from pool! Thread2");

                Statement stmt = connection.createStatement();
                stmt.execute("SELECT id from test");

                Thread.sleep(10000);
                MyConnectionPool.releaseConnection(connection);
                logger.info("Connection returned to pool. Thread2");
            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            }
            logger.info(Thread.currentThread().getName() + " Finished work");
        }, "Thread2");

        Thread thread3 = new Thread(()->{
            try {
                Connection connection = MyConnectionPool.getConnection();
                logger.info("Connection acquired from pool! Thread3");

                Statement stmt = connection.createStatement();
                stmt.execute("SELECT id from test");

                Thread.sleep(10000);
                MyConnectionPool.releaseConnection(connection);
                logger.info("Connection returned to pool. Thread3");
            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            }
            logger.info(Thread.currentThread().getName() + " Finished work");
        }, "Thread3");

        Thread thread4 = new Thread(()->{
            try {
                Connection connection = MyConnectionPool.getConnection();
                logger.info("Connection acquired from pool! Thread4");

                Statement stmt = connection.createStatement();
                stmt.execute("SELECT id from test");

                Thread.sleep(5000);
                MyConnectionPool.releaseConnection(connection);

                logger.info("Connection returned to pool. Thread4");
            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            }
            logger.info(Thread.currentThread().getName() + " Finished work");
        }, "Thread4");

        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.execute(thread1);
        executor.execute(thread2);
        executor.execute(thread3);
        executor.execute(thread4);

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        } finally {
            MyConnectionPool.closeAllConnections();
        }
    }
}
