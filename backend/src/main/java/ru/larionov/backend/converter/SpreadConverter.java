package ru.larionov.backend.converter;

import ru.larionov.backend.dto.spreads.SpreadDTO;
import ru.larionov.backend.model.Spread;

import java.text.SimpleDateFormat;

public class SpreadConverter {

    private static final SimpleDateFormat sdf =new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    public static SpreadDTO toDto(Spread spread) {
        SpreadDTO spreadDTO = new SpreadDTO();
        spreadDTO.setId(spread.getId());
        spreadDTO.setDescription(spread.getDescription());
        if (spread.getDateCreate() != null)
            spreadDTO.setDateCreate(sdf.format(spread.getDateCreate()));
        else
            spreadDTO.setDateCreate("-.-");
        if (spread.getDateFinish() != null)
            spreadDTO.setDateFinish(sdf.format(spread.getDateFinish()));
        else
            spreadDTO.setDateFinish("-.-");
        spreadDTO.setState(spread.getState());
        spreadDTO.setUSDT_amount_start(spread.getUSDT_amount_start());
        spreadDTO.setProfit(spread.getProfit());
        spreadDTO.setProfitPercent(spread.getProfitPercent());
        spreadDTO.setMaxProfit(spread.getMaxProfit());
        spreadDTO.setMaxProfitPercent(spread.getMaxProfitPercent());
        return spreadDTO;
    }
}
