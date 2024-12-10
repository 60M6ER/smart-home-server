package ru.larionov.backend.converter;

import com.poloniex.api.client.spot.model.response.spot.Price;
import ru.larionov.backend.model.PricePair;

public class PricePairConverter {

    public static PricePair fromPoloniexPricePair(Price price) {
        PricePair pricePair = new PricePair();
        pricePair.setPairToken(price.getSymbol());
        pricePair.setPrice(Double.parseDouble(price.getPrice()));
        return pricePair;
    }
}
