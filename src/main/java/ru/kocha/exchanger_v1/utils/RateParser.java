package ru.kocha.exchanger_v1.utils;

import ru.kocha.exchanger_v1.exception.ExceptionMessage;
import ru.kocha.exchanger_v1.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Optional;

public class RateParser {
    public static BigDecimal parseRate(String rate){

        if (rate == null || rate.isEmpty()){
            throw new ValidationException(ExceptionMessage.RATE_IS_NULL);
        }

        String regex = "^-?\\d+(\\.\\d+)?$";
        if (!rate.matches(regex)) {
            throw new ValidationException(ExceptionMessage.INVALID_RATE);
        }

        try {
            double rateDouble = Double.parseDouble(rate);
            return BigDecimal.valueOf(rateDouble);

        }  catch (NumberFormatException e) {
            throw new ValidationException(ExceptionMessage.INVALID_RATE);
        }
    }

    public static BigDecimal parseAndValidateRate(String rate) {
        BigDecimal rateDouble = parseRate(rate);
        Validator.validateExchangeRateValue(rateDouble);
        return rateDouble;
    }
}
