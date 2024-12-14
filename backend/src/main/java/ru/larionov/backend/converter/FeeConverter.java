package ru.larionov.backend.converter;

import com.poloniex.api.client.spot.model.response.spot.FeeInfo;
import ru.larionov.backend.dto.exchange.binance.BinanceAccountInfo;
import ru.larionov.backend.model.FeeInformation;

public class FeeConverter {

    public static FeeInformation fromPoloniexFee(FeeInfo feeInfo) {
        FeeInformation feeInformation = new FeeInformation();
        feeInformation.setMaker(Double.parseDouble(feeInfo.getMakerRate()));
        feeInformation.setTaker(Double.parseDouble(feeInfo.getTakerRate()));

        return feeInformation;
    }

    public static FeeInformation fromBinanceAccountInfo (BinanceAccountInfo binanceAccountInfo) {
        FeeInformation feeInformation = new FeeInformation();
        feeInformation.setMaker(Double.parseDouble(binanceAccountInfo.getCommissionRates().maker));
        feeInformation.setTaker(Double.parseDouble(binanceAccountInfo.getCommissionRates().taker));
        return feeInformation;
    }
}

