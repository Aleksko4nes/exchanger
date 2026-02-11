package ru.kocha.exchanger_v1.repository;

import ru.kocha.exchanger_v1.entities.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {
    List<Currency> getAllCurrencies() throws SQLException;
    Optional<Currency> getCurrencyByCode(String code) throws SQLException;
    Optional<Currency> addNewCurrency(String code, String fullname, String sign) throws SQLException;
}
