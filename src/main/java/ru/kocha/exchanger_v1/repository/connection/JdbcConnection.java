package ru.kocha.exchanger_v1.repository.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class JdbcConnection {

    private static final String URL = getRequiredEnv("POSTGRES_URL");
    private static final String USERNAME = getRequiredEnv("POSTGRES_USERNAME");
    private static final String PASSWORD = getRequiredEnv("POSTGRES_PASSWORD");

    private static final Long THIRTY_SECOND = 30L;
    private static final Long TEN_MINUTE = 60L;
    private static final Long THIRTY_MINUTE = 1800L;

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl(URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName("org.postgresql.Driver");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(THIRTY_SECOND);
        config.setIdleTimeout(TEN_MINUTE);
        config.setMaxLifetime(THIRTY_MINUTE);

        ds = new HikariDataSource(config);

        try (Connection connection = ds.getConnection()) {
            System.out.println("Database connection pool initialized successfully");
        } catch (SQLException e) {
            System.err.println("Database connection pool initialization failed");
            throw new RuntimeException("Database connection failed" + e);
        }
    }

    public static Connection getConnection() throws SQLException {
       return ds.getConnection();
    }

    private static String getRequiredEnv(String env) {
        String value = System.getenv(env);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Missing required environment variable " + env);
        }
        return value;
    }

    public static void closeConnection() {
        if (ds != null || !ds.isClosed()) {
            ds.close();
            System.out.println("Database connection closed successfully");
        }
    }

    private  JdbcConnection() {}
}
