package ru.kocha.exchanger_v1.service;

import ru.kocha.exchanger_v1.dto.request.CurrencyRequestDto;
import ru.kocha.exchanger_v1.dto.response.CurrencyResponseDto;
import ru.kocha.exchanger_v1.entities.Currency;
import ru.kocha.exchanger_v1.exception.ExceptionMessage;
import ru.kocha.exchanger_v1.exception.TransactionException;
import ru.kocha.exchanger_v1.repository.CurrencyRepository;
import ru.kocha.exchanger_v1.repository.UnitOfWork;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    private final CurrencyRepository repository;
    private final UnitOfWork unitOfWork;

    public CurrencyService(CurrencyRepository repository, UnitOfWork unitOfWork) {
        this.repository = repository;
        this.unitOfWork = unitOfWork;
    }

    public List<CurrencyResponseDto> getCurrencies() {

        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            List<Currency> currencies = repository.getAllCurrencies(connection);
            unitOfWork.commit();

            return currencies.stream()
                            .map(this::mapToDto)
                            .collect(Collectors.toList());

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new TransactionException(ExceptionMessage.GET_CURRENCY_EXCEPTION, 500);
        }
    }

    public CurrencyResponseDto getCurrencyByCode(String code) {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            Optional<Currency> optionalCurrency = repository.getCurrencyByCode(code, connection);

            if (optionalCurrency.isEmpty()) {
                throw new TransactionException(ExceptionMessage.MISSING_CURRENCY, 404);
            }

            Currency currency = optionalCurrency.get();
            unitOfWork.commit();
            return mapToDto(currency);

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new TransactionException(ExceptionMessage.GET_CURRENCY_EXCEPTION, 500);
        }
    }

    public CurrencyResponseDto createCurrency(CurrencyRequestDto currencyDto) {
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();

            Optional<Currency> existing = repository.getCurrencyByCode(currencyDto.code(), connection);

            if (existing.isPresent()) {
                unitOfWork.rollback();
                throw new TransactionException(ExceptionMessage.CURRENCY_ALREADY_EXISTS, 409);
            }

            Optional<Currency> savedCurrency = repository.addNewCurrency(
                    currencyDto.code(),
                    currencyDto.name(),
                    currencyDto.sign(),
                    connection);
            unitOfWork.commit();

            Currency currency = savedCurrency.get();
            return mapToDto(currency);
        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new TransactionException(ExceptionMessage.CREATING_CURRENCY_EXCEPTION, 500);
        }
    }

    private CurrencyResponseDto mapToDto(Currency currency) {
        return new CurrencyResponseDto(
                currency.getId().toString(),
                currency.getName(),
                currency.getCode(),
                currency.getSign());
    }
}
