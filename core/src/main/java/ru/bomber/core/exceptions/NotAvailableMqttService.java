package ru.bomber.core.exceptions;

public class NotAvailableMqttService extends RuntimeException{

    private static final String PREFIX = "MQTT service is not available. ";

    public NotAvailableMqttService(String message) {
        super(PREFIX + message);
    }

    public NotAvailableMqttService(String message, Throwable cause) {
        super(PREFIX + message, cause);
    }

    public NotAvailableMqttService(Throwable cause) {
        super(PREFIX, cause);
    }
}
