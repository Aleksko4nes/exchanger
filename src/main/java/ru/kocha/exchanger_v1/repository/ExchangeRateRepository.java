package ru.kocha.exchanger_v1.repository;

import ru.kocha.exchanger_v1.entities.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository {
    List<ExchangeRate> findByCodeWithUsdBase(String baseCurrencyCode, String targetCurrencyCode);
    List<ExchangeRate> getExchangeRates();
    Optional<ExchangeRate> getExchangeRateByCode(String baseCode, String targetCode) throws SQLException;
    Optional<ExchangeRate> addNewExchangeRate(String baseCode, String targetCode, BigDecimal rate) throws SQLException;
    Optional<ExchangeRate> updateExchangerRate(String baseCode, String targetCode, BigDecimal rate);
}
