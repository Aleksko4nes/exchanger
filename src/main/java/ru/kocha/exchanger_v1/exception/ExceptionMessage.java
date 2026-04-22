package ru.kocha.exchanger_v1.exception;

public final class ExceptionMessage {
    private ExceptionMessage() {}

    public static final String INVALID_CURRENCY_CODE = "Invalid currency code format";
    public static final String MISSING_CURRENCY_CODE = "Currency code not found";
    public static final String MISSING_CURRENCY_NAME = "Currency name not found";
    public static final String INVALID_CURRENCY_NAME = "Invalid currency name";
    public static final String MISSING_CURRENCY_SIGN = "Currency sign not found";
    public static final String INVALID_CURRENCY_SIGN = "Invalid currency sign";
    public static final String CURRENCY_ALREADY_EXISTS = "Currency already exists";
    public static final String CREATING_CURRENCY_EXCEPTION = "Database error while creating new currency";
    public static final String GET_CURRENCY_EXCEPTION = "Database error while getting currency";
    public static final String MISSING_CURRENCY = "Currency not found";

    public static final String MISSING_EXCHANGE_RATE_CODES = "Missing exchange rate codes";
    public static final String INVALID_EXCHANGE_RATE_CODE =  "Invalid exchange rate code format";
    public static final String MISSING_EXCHANGE_RATE = "Exchange rate not found";
    public static final String GET_EXCHANGE_RATE_EXCEPTION = "Database error while getting exchange rate";
    public static final String RATE_IS_NULL = "Rate is null";
    public static final String INVALID_RATE = "Invalid rate format";
    public static final String UPDATE_EXCHANGE_RATE_EXCEPTION = "Database error while updating exchange rate";
    public static final String GET_ALL_EXCHANGE_RATES_EXCEPTION = "Database error while getting all exchange rates";
    public static final String EXCHANGE_RATE_ALREADY_EXIST = "Exchange rate already exists";
    public static final String ADD_NEW_EXCHANGE_RATE_EXCEPTION = "Database error while adding new exchange rate";

    public static final String CONVERT_EXCEPTION = "Converting exception";
}
