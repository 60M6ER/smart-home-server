package ru.larionov.backend.exception;

public class NetworksAreNotEquals extends Exception{
    public NetworksAreNotEquals() {
    }

    public NetworksAreNotEquals(String message) {
        super(message);
    }
}
