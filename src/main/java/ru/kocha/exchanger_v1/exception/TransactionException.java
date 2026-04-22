package ru.kocha.exchanger_v1.exception;

public class TransactionException extends RuntimeException {
    private final int code;

    public TransactionException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
