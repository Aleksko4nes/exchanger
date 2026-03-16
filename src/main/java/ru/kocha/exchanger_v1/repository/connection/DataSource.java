package ru.kocha.exchanger_v1.repository.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DataSource {

    private static final String URL = System.getenv("POSTGRES_URL");
    private static final String USERNAME = System.getenv("POSTGRES_NAME");
    private static final String PASSWORD = System.getenv("POSTGRES_PASSWORD");

    private static final Long THIRTY_SECOND = 30000L;
    private static final Long TEN_MINUTE = 60000L;
    private static final Long THIRTY_MINUTE = 1800000L;

    private final static HikariConfig CONFIG = new HikariConfig();
    private static final HikariDataSource DATA_SOURCE;

    static {
        CONFIG.setJdbcUrl(URL);
        CONFIG.setUsername(USERNAME);
        CONFIG.setPassword(PASSWORD);
        CONFIG.setDriverClassName("org.postgresql.Driver");

        CONFIG.setMaximumPoolSize(10);
        CONFIG.setMinimumIdle(2);
        CONFIG.setConnectionTimeout(THIRTY_SECOND);
        CONFIG.setIdleTimeout(TEN_MINUTE);
        CONFIG.setMaxLifetime(THIRTY_MINUTE);

        DATA_SOURCE = new HikariDataSource(CONFIG);
    }

    public Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
