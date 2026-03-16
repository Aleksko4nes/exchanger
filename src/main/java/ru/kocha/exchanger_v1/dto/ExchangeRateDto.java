package ru.kocha.exchanger_v1.dto;

public record ExchangeRateDto(String baseCode, String targetCode, java.math.BigDecimal rate) {
}
