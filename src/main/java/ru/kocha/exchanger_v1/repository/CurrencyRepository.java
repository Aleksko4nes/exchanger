package ru.kocha.exchanger_v1.repository;

import ru.kocha.exchanger_v1.entities.Currency;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {
    List<Currency> getAllCurrencies(Connection connection) throws SQLException;
    Optional<Currency> getCurrencyByCode(String code, Connection connection) throws SQLException;
    Optional<Currency> addNewCurrency(String code, String fullName, String sign, Connection connection) throws SQLException;
}
