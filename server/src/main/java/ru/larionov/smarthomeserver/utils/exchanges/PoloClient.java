package ru.larionov.smarthomeserver.utils.exchanges;

public class PoloClient {

    private String APIKey;
    private String secretKey;

    public PoloClient(String APIKey, String secretKey) {
        this.APIKey = APIKey;
        this.secretKey = secretKey;
    }
}
