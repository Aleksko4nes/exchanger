package ru.kocha.exchanger_v1.service;

import ru.kocha.exchanger_v1.dto.CurrencyCodePair;
import ru.kocha.exchanger_v1.dto.request.ExchangeRateRequestDto;
import ru.kocha.exchanger_v1.dto.response.CurrencyResponseDto;
import ru.kocha.exchanger_v1.dto.response.ExchangeRateResponseDto;
import ru.kocha.exchanger_v1.entities.Currency;
import ru.kocha.exchanger_v1.entities.ExchangeRate;
import ru.kocha.exchanger_v1.exception.ExceptionMessage;
import ru.kocha.exchanger_v1.exception.TransactionException;
import ru.kocha.exchanger_v1.repository.ExchangeRateRepository;
import ru.kocha.exchanger_v1.repository.UnitOfWork;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExchangeRateService {
    private final ExchangeRateRepository repository;
    private final UnitOfWork unitOfWork;

    public ExchangeRateService(ExchangeRateRepository repository, UnitOfWork unitOfWork) {
        this.repository = repository;
        this.unitOfWork = unitOfWork;
    }

    public List<ExchangeRateResponseDto> getAllExchangeRates() {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            List<ExchangeRate> exchangeRates = repository.getExchangeRates(connection);
            unitOfWork.commit();
            return exchangeRates.stream()
                            .map(this::mapToDto)
                            .collect(Collectors.toList());

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new TransactionException(ExceptionMessage.GET_ALL_EXCHANGE_RATES_EXCEPTION, 500);
        }
    }

    public ExchangeRateResponseDto getExchangeRateByCodes(CurrencyCodePair requestDto) {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();

            Optional<ExchangeRate> exchangeRateOptional = repository.getExchangeRateByCode(
                    requestDto.from(),
                    requestDto.to(),
                    connection);

            if (exchangeRateOptional.isEmpty()) {
                unitOfWork.rollback();
                throw new TransactionException(ExceptionMessage.MISSING_EXCHANGE_RATE, 404);
            }

            ExchangeRate exchangeRate = exchangeRateOptional.get();
            unitOfWork.commit();
            return mapToDto(exchangeRate);

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new TransactionException(ExceptionMessage.GET_EXCHANGE_RATE_EXCEPTION, 500);
        }
    }


    public ExchangeRateResponseDto addNewExchangeRate(ExchangeRateRequestDto requestDto) {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            Optional<ExchangeRate> exchangeRateOptional = repository.getExchangeRateByCode(
                    requestDto.from(),
                    requestDto.to(),
                    connection);
            if (exchangeRateOptional.isPresent()) {
                unitOfWork.rollback();
                throw new TransactionException(ExceptionMessage.EXCHANGE_RATE_ALREADY_EXIST, 409);
            }

            Optional<ExchangeRate> savedExchangeRate = repository.addNewExchangeRate(
                    requestDto.from(),
                    requestDto.to(),
                    requestDto.rate(),
                    connection);
            unitOfWork.commit();

            ExchangeRate exchangeRate = savedExchangeRate.get();
            return  mapToDto(exchangeRate);

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new TransactionException(ExceptionMessage.ADD_NEW_EXCHANGE_RATE_EXCEPTION, 500);
        }
    }

    public ExchangeRateResponseDto updateExchangeRate(ExchangeRateRequestDto requestDto) {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();

            Optional<ExchangeRate> optionalExchangeRate = repository.getExchangeRateByCode(
                    requestDto.from(),
                    requestDto.to(),
                    connection);

            if (optionalExchangeRate.isEmpty()) {
                unitOfWork.rollback();
                throw new TransactionException(ExceptionMessage.MISSING_EXCHANGE_RATE, 404);
            }

            Optional<ExchangeRate> updatedExchangeRate = repository.updateExchangerRate(
                    requestDto.from(),
                    requestDto.to(),
                    requestDto.rate(),
                    connection);

            unitOfWork.commit();

            ExchangeRate exchangeRate = updatedExchangeRate.get();
            return mapToDto(exchangeRate);

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new TransactionException(ExceptionMessage.UPDATE_EXCHANGE_RATE_EXCEPTION, 500);
        }
    }

    private ExchangeRateResponseDto mapToDto(ExchangeRate exchangeRate) {
        Currency base = exchangeRate.getBaseCurrency();
        CurrencyResponseDto baseResponseDto = new CurrencyResponseDto(
                base.getId().toString(),
                base.getName(),
                base.getCode(),
                base.getSign());

        Currency target = exchangeRate.getTargetCurrency();
        CurrencyResponseDto targetResponseDto = new CurrencyResponseDto(
                target.getId().toString(),
                target.getName(),
                target.getCode(),
                target.getSign());

        return new ExchangeRateResponseDto(exchangeRate.getId().toString(),
                baseResponseDto,
                targetResponseDto,
                exchangeRate.getRate().toString());
    }
}
