package ru.kocha.exchanger_v1.service;

import ru.kocha.exchanger_v1.dto.ExchangeRateRequestDto;
import ru.kocha.exchanger_v1.dto.ExchangeRateDto;
import ru.kocha.exchanger_v1.entities.ExchangeRate;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepository;
import ru.kocha.exchanger_v1.repository.UnitOfWork;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateRepository repository;
    private final UnitOfWork unitOfWork;

    public ExchangeRateService(ExchangeRateRepository repository, UnitOfWork unitOfWork) {
        this.repository = repository;
        this.unitOfWork = unitOfWork;
    }

    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates;

        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();

            exchangeRates = repository.getExchangeRates(connection);
            unitOfWork.commit();
        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    public ExchangeRate getExchangeRateByCodes(ExchangeRateDto exchangeRateDto) {
        ExchangeRate exchangeRate;
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();

            Optional<ExchangeRate> exchangeRateOptional = repository.getExchangeRateByCode(
                    exchangeRateDto.baseCode(),
                    exchangeRateDto.targetCode(),
                    connection);

            if (exchangeRateOptional.isEmpty()) {
                unitOfWork.rollback();
                throw new RuntimeException("exchange rate not found");
            } else {
                exchangeRate = exchangeRateOptional.get();
                unitOfWork.commit();
            }
        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        }
        return exchangeRate;
    }

    public ExchangeRate addNewExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto) {
        ExchangeRate exchangeRate;
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();

            Optional<ExchangeRate> exchangeRateOptional = repository.getExchangeRateByCode(
                    exchangeRateRequestDto.baseCode(),
                    exchangeRateRequestDto.targetCode(),
                    connection);
            if (exchangeRateOptional.isPresent()) {
                unitOfWork.rollback();
                throw new RuntimeException("exchange rate already exists");
            }

            Optional<ExchangeRate> savedExchangeRate = repository.addNewExchangeRate(
                    exchangeRateRequestDto.baseCode(),
                    exchangeRateRequestDto.targetCode(),
                    exchangeRateRequestDto.rate(),
                    connection);

            unitOfWork.commit();
            exchangeRate = savedExchangeRate.get();

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        }
        return exchangeRate;
    }

    public ExchangeRate updateExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto) {
        ExchangeRate exchangeRate;
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();

            Optional<ExchangeRate> optionalExchangeRate = repository.getExchangeRateByCode(
                    exchangeRateRequestDto.baseCode(),
                    exchangeRateRequestDto.targetCode(),
                    connection);
            if (optionalExchangeRate.isEmpty()) {
                unitOfWork.rollback();
                throw new RuntimeException("exchange rate not found");
            }

            Optional<ExchangeRate> updatedExchangeRate = repository.updateExchangerRate(
                    exchangeRateRequestDto.baseCode(),
                    exchangeRateRequestDto.targetCode(),
                    exchangeRateRequestDto.rate(),
                    connection);

            unitOfWork.commit();
            exchangeRate = updatedExchangeRate.get();

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        }
        return exchangeRate;
    }
}
