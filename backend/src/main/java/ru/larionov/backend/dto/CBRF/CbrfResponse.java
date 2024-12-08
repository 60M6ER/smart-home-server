package ru.larionov.backend.dto.CBRF;

import lombok.Data;

@Data
public class CbrfResponse {
    private String disclaimer;
    private String date;
    private long timestamp;
    private String base;
    private Valute rates;
}
