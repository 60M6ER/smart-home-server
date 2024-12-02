package ru.bomber.trader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeeData {
    private Double makerFee;
    private Double takerFee;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FeeData{");
        sb.append("makerFee=").append(makerFee);
        sb.append(", takerFee=").append(takerFee);
        sb.append('}');
        return sb.toString();
    }
}
