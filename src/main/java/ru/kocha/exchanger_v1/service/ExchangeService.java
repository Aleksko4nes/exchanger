package ru.kocha.exchanger_v1.service;

import ru.kocha.exchanger_v1.entities.ExchangeRate;
import ru.kocha.exchanger_v1.entities.ExchangeResponse;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepository;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepositoryImpl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeService {

    private final ExchangeRateRepository repository = new ExchangeRateRepositoryImpl();

    public ExchangeResponse convert(String baseCurrencyCode,
                                    String targetCurrencyCode,
                                    BigDecimal amount) throws SQLException {

        ExchangeRate exchangeRate = getExchangeRate(baseCurrencyCode, targetCurrencyCode).orElseThrow();
        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate()
                .setScale(2, RoundingMode.HALF_EVEN));

        return new ExchangeResponse(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                convertedAmount);
    }

    private Optional<ExchangeRate> getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> exchangeRate = getFromDirectExchangeRate(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate.isEmpty()) {
            exchangeRate = getFromReversedExchangeRate(baseCurrencyCode, targetCurrencyCode);
        }
        if (exchangeRate.isEmpty()) {
            exchangeRate = getFromCrossExchangeRate(baseCurrencyCode, targetCurrencyCode);
        }
        return exchangeRate;
    }

    private Optional<ExchangeRate> getFromDirectExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return repository.getExchangeRateByCode(baseCurrencyCode, targetCurrencyCode);
    }

    private Optional<ExchangeRate> getFromReversedExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> reversedExchangeRateOption = repository.getExchangeRateByCode(targetCurrencyCode, baseCurrencyCode);

        if (reversedExchangeRateOption.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate reverserdExchangeRate = reversedExchangeRateOption.get();

        ExchangeRate directExchangeRate = new ExchangeRate(
                reverserdExchangeRate.getTargetCurrency(),
                reverserdExchangeRate.getBaseCurrency(),
                BigDecimal.ONE.divide(reverserdExchangeRate.getRate(), MathContext.DECIMAL64)
        );

        return Optional.of(directExchangeRate);
    }

    private Optional<ExchangeRate> getFromCrossExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        List<ExchangeRate> ratesWithUsdBase = repository.findByCodeWithUsdBase(baseCurrencyCode, targetCurrencyCode);

        ExchangeRate usdToBaseExchange = getExchangeForCode(ratesWithUsdBase, baseCurrencyCode);
        ExchangeRate usdToTargetExchange = getExchangeForCode(ratesWithUsdBase, targetCurrencyCode);

        BigDecimal baseToTargetRate = usdToTargetExchange.getRate()
                .divide(usdToBaseExchange.getRate(), MathContext.DECIMAL64);

        ExchangeRate exchangeRate = new ExchangeRate(
                usdToBaseExchange.getTargetCurrency(),
                usdToTargetExchange.getTargetCurrency(),
                baseToTargetRate
        );

        return Optional.of(exchangeRate);
    }

    private ExchangeRate getExchangeForCode(List<ExchangeRate> rates, String code) {
        return rates.stream()
                .filter(rate -> rate.getTargetCurrency().getCode().equals(code))
                .findFirst()
                .orElseThrow();
    }
}
