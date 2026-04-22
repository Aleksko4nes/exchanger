package ru.kocha.exchanger_v1.dto.response;

import ru.kocha.exchanger_v1.entities.Currency;

import java.math.BigDecimal;

public record ExchangeResponse (Currency baseCurrency,
                                Currency targetCurrency,
                                BigDecimal exchangeRate,
                                BigDecimal amount,
                                BigDecimal convertedAmount) {
}
