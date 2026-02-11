package ru.kocha.exchanger_v1.utils;

import java.math.BigDecimal;
import java.util.Optional;

public class RateParser {
    public static Optional<BigDecimal> parseRate(String rate){
        if (rate == null || rate.isEmpty()){
            return Optional.empty();
        }
        String regex = "^-?\\d+(\\.\\d+)?$";

        if (!rate.matches(regex)) {
            return Optional.empty();
        }

        try {
            double rateDouble = Double.parseDouble(rate);
            return Optional.of(BigDecimal.valueOf(rateDouble));
        }  catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
