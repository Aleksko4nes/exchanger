package ru.kocha.exchanger_v1.repository.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class JdbcConnection {

    private static final String URL = System.getenv("POSTGRES_URL");
    private static final String USERNAME = System.getenv("POSTGRES_USERNAME");
    private static final String PASSWORD = System.getenv("POSTGRES_PASSWORD");

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl(URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName("org.postgresql.Driver");
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
       return ds.getConnection();
    }

    private  JdbcConnection() {}
}
