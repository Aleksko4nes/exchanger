package ru.kocha.exchanger_v1.dto.request;

import java.math.BigDecimal;

public record ExchangeRateRequestDto (String from, String to, BigDecimal rate) {
}
