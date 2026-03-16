package ru.kocha.exchanger_v1.dto;

import java.math.BigDecimal;

public record ExchangeRequest (String from, String to, BigDecimal amount) {
}
