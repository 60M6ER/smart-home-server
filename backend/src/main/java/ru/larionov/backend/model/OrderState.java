package ru.larionov.backend.model;

public enum OrderState {
    NEW, PARTIALLY_FILLED, FILLED, PENDING_CANCEL, PARTIALLY_CANCELED, CANCELED, FAILED
}
