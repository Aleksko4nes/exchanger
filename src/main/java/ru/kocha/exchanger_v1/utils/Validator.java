package ru.kocha.exchanger_v1.utils;

import java.util.Optional;

public class Validator {

    public static Optional<String> validateCurrencyCode(String code) {
        String validCode = code;
        if (!code.isEmpty() && code.charAt(0) == '/') {
            validCode = code.substring(1);
        }
        if (!validCode.matches("[A-Za-z]{3}$")) {
            return Optional.empty();
        }
        return Optional.of(validCode.toUpperCase());
    }

    public static Optional<String> validateExchangeRateCodes(String code) {
        String validCode = code;
        if (!code.isEmpty() && code.charAt(0) == '/') {
            validCode = code.substring(1);
        }
        if (!validCode.matches("[A-Za-z]{6}$")) {
            return Optional.empty();
        }
        return Optional.of(validCode.toUpperCase());
    }
}
