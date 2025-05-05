package com.expensemanager.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseManager.class);
    private static final String DB_URL = "jdbc:sqlite:expense_manager.db";
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL);
                logger.info("Connected to SQLite database");
            } catch (SQLException e) {
                logger.error("Error connecting to database", e);
            }
        }
        return connection;
    }




    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createExpensesTable = "CREATE TABLE IF NOT EXISTS expenses (" +
                    "id TEXT PRIMARY KEY," +
                    "amount TEXT NOT NULL," +
                    "category TEXT NOT NULL," +
                    "date TEXT NOT NULL" +
                    ")";

            stmt.execute(createExpensesTable);
            logger.info("Database tables created successfully");

        } catch (SQLException e) {
            logger.error("Error initializing database", e);
        }finally {
            closeConnection();
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
}