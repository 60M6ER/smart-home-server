package ru.bomber.trader.utils;

public class PoloAPI implements ExchangeAPI {

    private String APIKey;
    private String secret;

    public PoloAPI(String APIKey, String secret) {
        this.APIKey = APIKey;
        this.secret = secret;
    }
}
