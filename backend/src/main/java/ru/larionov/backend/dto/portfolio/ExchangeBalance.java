package ru.larionov.backend.dto.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeBalance {
    private String exchange;
    private double usd;
}
