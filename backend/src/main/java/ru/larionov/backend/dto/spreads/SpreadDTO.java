package ru.larionov.backend.dto.spreads;

import lombok.Data;
import ru.larionov.backend.model.SpreadState;
import java.util.UUID;

@Data
public class SpreadDTO {
    private UUID id;
    private String description;
    private String dateCreate;
    private String dateFinish;
    private SpreadState state;
    private double USDT_amount_start;
    private double profit;
    private double profitPercent;
    private double maxProfit;
    private double maxProfitPercent;
}
