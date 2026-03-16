package ru.kocha.exchanger_v1.service;

import ru.kocha.exchanger_v1.dto.CurrencyRequestDto;
import ru.kocha.exchanger_v1.entities.Currency;
import ru.kocha.exchanger_v1.repository.CurrencyRepository;
import ru.kocha.exchanger_v1.repository.UnitOfWork;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyRepository repository;
    private final UnitOfWork unitOfWork;

    public CurrencyService(CurrencyRepository repository, UnitOfWork unitOfWork) {
        this.repository = repository;
        this.unitOfWork = unitOfWork;
    }

    public List<Currency> getCurrencies() {
        List<Currency> currencies;
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            currencies = repository.getAllCurrencies(connection);
            unitOfWork.commit();
            if (currencies.isEmpty()) {
                throw new RuntimeException("Пока нет в бд");
            }
        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        }
        return currencies;
    }

    public Currency getCurrencyByCode(String code) {
        Currency currency = null;
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();
            Optional<Currency> optionalCurrency = repository.getCurrencyByCode(code, connection);
            if (optionalCurrency.isPresent()) {
                currency = optionalCurrency.get();
            }
            unitOfWork.commit();
        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        }
        return currency;
    }

    public Currency createCurrency(CurrencyRequestDto currencyDto) {
        Currency currency;
        try {
            unitOfWork.start();
            Connection connection = unitOfWork.getConnection();

            Optional<Currency> existing = repository.getCurrencyByCode(currencyDto.code(), connection);
            if (existing.isPresent()) {
                unitOfWork.rollback();
                throw new RuntimeException("Currency already exists");
            }

            Optional<Currency> savedCurrency = repository.addNewCurrency(
                    currencyDto.code(),
                    currencyDto.fullName(),
                    currencyDto.sign(),
                    connection);

            unitOfWork.commit();

            currency = savedCurrency.get();

        } catch (SQLException e) {
            unitOfWork.rollback();
            throw new RuntimeException(e);
        }

        return currency;
    }
}
