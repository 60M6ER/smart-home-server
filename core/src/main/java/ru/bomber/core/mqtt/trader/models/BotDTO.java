package ru.bomber.core.mqtt.trader.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class BotDTO {

    private UUID id;
    private String name;
    private String pair;
    private String baseInstrument;
    private Double minAmount;
    private Boolean amountAtPercent;
    private Double profit;
    private Double stepToBay;
    private Boolean stepAtPercent;
    private Boolean equalSteps;
    private StrategyType strategyType;
    private Boolean active;
    private ExchangeVendor vendor;
}
