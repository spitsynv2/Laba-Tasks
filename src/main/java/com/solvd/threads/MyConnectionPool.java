package com.solvd.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Vadym Spitsyn
 * @created 2025-01-02
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;


public class MyConnectionPool {
    private static final Logger logger = LogManager.getLogger(MyConnectionPool.class);
    private static final String URL = "jdbc:mysql://localhost:3306/my_database";
    private static final String USER = "Admin";
    private static final String PASSWORD = "Admin";
    private static final int SIZE = 3;
    private static ArrayBlockingQueue<Connection> connections;
    private static MyConnectionPool instance;

    private MyConnectionPool(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connections = new ArrayBlockingQueue<>(SIZE);

            for (int i = 0; i < SIZE; i++) {
                connections.offer(DriverManager.getConnection(URL, USER, PASSWORD));
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    public static MyConnectionPool getInstance() {
        if (instance == null) {
            instance = new MyConnectionPool();
        }
        return instance;
    }

    public static Connection getConnection() throws InterruptedException {
        return connections.take();
    }

    public static void releaseConnection(Connection connection) {
        if (connection != null) {
            connections.offer(connection);
        }
    }

    public static void closeAllConnections() {
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error(e);
            }
        }
        logger.info("Closing all connections");
    }
}
