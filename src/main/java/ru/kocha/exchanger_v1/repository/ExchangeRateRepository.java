package ru.kocha.exchanger_v1.repository;

import ru.kocha.exchanger_v1.entities.ExchangeRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository {
    List<ExchangeRate> findByCodeWithUsdBase(String baseCurrencyCode, String targetCurrencyCode, Connection connection) throws SQLException;
    List<ExchangeRate> getExchangeRates(Connection connection) throws SQLException;
    Optional<ExchangeRate> getExchangeRateByCode(String baseCode, String targetCode, Connection connection) throws SQLException;
    Optional<ExchangeRate> addNewExchangeRate(String baseCode, String targetCode, BigDecimal rate, Connection connection) throws SQLException;
    Optional<ExchangeRate> updateExchangerRate(String baseCode, String targetCode, BigDecimal rate, Connection connection);
}
