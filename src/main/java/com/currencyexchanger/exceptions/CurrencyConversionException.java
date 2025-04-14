package com.currencyexchanger.exceptions;

public class CurrencyConversionException extends RuntimeException {

    public CurrencyConversionException(String message) {
        super(message);
    }

    public CurrencyConversionException(String message, Exception e) {
        super(message, e);
    }
}
