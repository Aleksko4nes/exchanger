package ru.kocha.exchanger_v1.service;

import ru.kocha.exchanger_v1.dto.request.ExchangeRequest;
import ru.kocha.exchanger_v1.entities.ExchangeRate;
import ru.kocha.exchanger_v1.dto.response.ExchangeResponse;
import ru.kocha.exchanger_v1.exception.ExceptionMessage;
import ru.kocha.exchanger_v1.exception.TransactionException;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepository;
import ru.kocha.exchanger_v1.repository.UnitOfWork;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeService {

    private final ExchangeRateRepository repository;
    private final UnitOfWork unitOfWork;

    public ExchangeService(ExchangeRateRepository repository, UnitOfWork unitOfWork) {
        this.repository = repository;
        this.unitOfWork = unitOfWork;
    }

    public ExchangeResponse convert(ExchangeRequest request)  {
        String baseCurrencyCode = request.from();
        String targetCurrencyCode = request.to();
        BigDecimal amount = request.amount();

        ExchangeRate exchangeRate;
        try {
            exchangeRate = getExchangeRate(baseCurrencyCode, targetCurrencyCode).orElseThrow();
        } catch (SQLException e) {
            throw new TransactionException(ExceptionMessage.CONVERT_EXCEPTION, 500);
        }
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

    private Optional<ExchangeRate> getFromDirectExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            return repository.getExchangeRateByCode(
                    baseCurrencyCode,
                    targetCurrencyCode,
                    connection);
        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        } finally {
            unitOfWork.commit();
        }
    }

    private Optional<ExchangeRate> getFromReversedExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            Optional<ExchangeRate> reversedExchangeRateOption = repository.getExchangeRateByCode(
                    targetCurrencyCode,
                    baseCurrencyCode,
                    connection);

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

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        } finally {
            unitOfWork.commit();
        }
    }

    private Optional<ExchangeRate> getFromCrossExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            List<ExchangeRate> ratesWithUsdBase = repository.findByCodeWithUsdBase(
                    baseCurrencyCode,
                    targetCurrencyCode,
                    connection);

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

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        } finally {
            unitOfWork.commit();
        }
    }

    private ExchangeRate getExchangeForCode(List<ExchangeRate> rates, String code) {
        return rates.stream()
                .filter(rate -> rate.getTargetCurrency().getCode().equals(code))
                .findFirst()
                .orElseThrow();
    }
}
