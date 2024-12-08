package ru.larionov.backend.exception;

public class ExchangeNotFoundByVendor extends RuntimeException{
    public ExchangeNotFoundByVendor(String message) {
        super(message);
    }
}
