package ru.kocha.exchanger_v1.repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface UnitOfWork {
    void start();
    void commit();
    void rollback();
    Connection getConnection();
}
