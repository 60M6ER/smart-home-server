package ru.larionov.backend.dto.hitbtc;

import lombok.Data;

@Data
public class HitBTCSymbol {
    private String symbol;
    private String type;
    private String base_currency;
    private String quote_currency;
    private String status;
    private String quantity_increment;
    private String tick_size;
    private String take_rate;
    private String make_rate;
    private String fee_currency;
    private boolean margin_trading;
    private double max_initial_leverage;
    private String contract_type;
    private Object expiry;
    private String underlying;
    private String instruments_tab_name;
}
