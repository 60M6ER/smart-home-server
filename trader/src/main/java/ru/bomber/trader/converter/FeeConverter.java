package ru.bomber.trader.converter;

import com.poloniex.api.client.model.FeeInfo;
import ru.bomber.trader.dto.FeeData;

public class FeeConverter {
    public static FeeData toDTO(FeeInfo feeInfo) {
        return new FeeData(
                Double.parseDouble(feeInfo.getMakerRate()),
                Double.parseDouble(feeInfo.getTakerRate())
        );
    }
}
