package ru.kocha.exchanger_v1.repository;

import java.sql.Connection;

public interface UnitOfWork {
    void start();
    void commit();
    void rollback();
    Connection getConnection();
}
