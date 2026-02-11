package ru.kocha.exchanger_v1.repository.connection;

import ru.kocha.exchanger_v1.utils.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class JdbcConnection {

    private static final String URL = "database.url";
    private static final String USERNAME = "database.username";
    private static final String PASSWORD =  "database.password";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(
                    PropertiesUtil.getProperty(URL),
                    PropertiesUtil.getProperty(USERNAME),
                    PropertiesUtil.getProperty(PASSWORD));
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong when trying to connect to database.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private  JdbcConnection() {}
}
