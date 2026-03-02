package ru.kocha.exchanger_v1.entities;

import java.math.BigDecimal;

public class ExchangeResponse {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal exchangeRate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public ExchangeResponse(Currency baseCurrency,
                            Currency targetCurrency,
                            BigDecimal exchangeRate,
                            BigDecimal amount,
                            BigDecimal convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
