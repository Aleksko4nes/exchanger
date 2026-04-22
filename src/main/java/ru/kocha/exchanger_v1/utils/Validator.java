package ru.kocha.exchanger_v1.utils;

import ru.kocha.exchanger_v1.exception.ExceptionMessage;
import ru.kocha.exchanger_v1.exception.ValidationException;

import java.math.BigDecimal;

public class Validator {

    private static final String EXCHANGE_RATE_CODE_PATTERN = "/[A-Z]{6}$";
    private static final String CODE_PATTERN = "[A-Z]{3}$";
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 60;
    private static final int SIGN_LENGTH = 3;
    private static final int MAX_RATE_LENGTH = 7;

    public static void validateCurrencyCode(String code) {
        if (code.isEmpty()) {
            throw new ValidationException(ExceptionMessage.MISSING_CURRENCY_CODE);
        }

        if (!code.matches(CODE_PATTERN)) {
            throw new ValidationException(ExceptionMessage.INVALID_CURRENCY_CODE);
        }
    }

    public static void validateCurrencyName(String name) {
        if (name.trim().isEmpty()) {
            throw new ValidationException(ExceptionMessage.MISSING_CURRENCY_NAME);
        }

        if (name.length() < MIN_NAME_LENGTH && name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(ExceptionMessage.INVALID_CURRENCY_NAME);
        }
    }

    public static void validateCurrencySign(String sign) {
        if (sign == null || sign.isEmpty()) {
            throw new ValidationException(ExceptionMessage.MISSING_CURRENCY_SIGN);
        }

        if (sign.length() > SIGN_LENGTH) {
            throw new ValidationException(ExceptionMessage.INVALID_CURRENCY_SIGN);
        }
    }

    public static void validateExchangeRateCodes(String code) {

        if (code.isEmpty()) {
            throw new ValidationException(ExceptionMessage.MISSING_EXCHANGE_RATE_CODES);
        }

        if (!code.matches(EXCHANGE_RATE_CODE_PATTERN)) {
            throw new ValidationException(ExceptionMessage.INVALID_EXCHANGE_RATE_CODE);
        }
    }

    public static void validateExchangeRateValue(BigDecimal rate) {
        if (rate == null) {
            throw new ValidationException(ExceptionMessage.MISSING_EXCHANGE_RATE);
        }

        if (rate.compareTo(BigDecimal.valueOf(MAX_RATE_LENGTH)) > 0 ) {
            throw new ValidationException(ExceptionMessage.INVALID_RATE);
        }
    }
}
