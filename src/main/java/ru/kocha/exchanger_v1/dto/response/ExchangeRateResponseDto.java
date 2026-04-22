package ru.kocha.exchanger_v1.dto.response;


public record ExchangeRateResponseDto (String id, CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency, String rate) {
}
