package ru.kocha.exchanger_v1.repository;

import ru.kocha.exchanger_v1.repository.connection.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class UnitOfWorkImpl implements UnitOfWork {
    private final DataSource dataSource;
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public UnitOfWorkImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void start() {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            connectionHolder.set(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Could not start transaction", e);
        }
    }

    @Override
    public void commit() {
        Connection connection = connectionHolder.get();
        if (connection != null) {
            try {
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException("Commit failed", e);
            } finally {
                closeConnection(connection);
            }
        }
    }

    @Override
    public void rollback() {
        Connection connection = connectionHolder.get();
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection(connection);
            }
        }
    }

    @Override
    public Connection getConnection() {
        Connection connection = connectionHolder.get();
        if (connection == null) {
            throw new IllegalStateException("Transaction is not started");
        }
        return connection;
    }

    private void closeConnection(Connection connection) {
        try {
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionHolder.remove();
        }
    }
}
