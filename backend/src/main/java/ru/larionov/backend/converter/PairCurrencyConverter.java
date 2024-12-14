package ru.larionov.backend.converter;

import com.poloniex.api.client.spot.model.response.spot.Market;
import ru.larionov.backend.dto.exchange.binance.BinanceSymbol;
import ru.larionov.backend.dto.hitbtc.HitBTCSymbol;
import ru.larionov.backend.model.PairCurrency;

public class PairCurrencyConverter {

    public static PairCurrency fromPoloniexMarket(Market market) {
        PairCurrency pairCurrency = new PairCurrency();
        pairCurrency.setBaseCurrency(market.getBaseCurrencyName());
        pairCurrency.setQuoteCurrency(market.getQuoteCurrencyName());
        pairCurrency.setPriceScale(market.getSymbolTradeLimit().getPriceScale());
        pairCurrency.setAmountScale(market.getSymbolTradeLimit().getAmountScale());
        pairCurrency.setQuantityScale(market.getSymbolTradeLimit().getQuantityScale());
        pairCurrency.setMinQuantity(Double.parseDouble(market.getSymbolTradeLimit().getMinQuantity()));
        pairCurrency.setMinAmount(Double.parseDouble(market.getSymbolTradeLimit().getMinAmount()));
        pairCurrency.setExchangeToken(market.getSymbol());
        pairCurrency.setMarginTrading(market.getCrossMargin().getSupportCrossMargin());
        return pairCurrency;
    }

    public static PairCurrency fromBinanceSymbol(BinanceSymbol binanceSymbol) {
        PairCurrency pairCurrency = new PairCurrency();
        pairCurrency.setBaseCurrency(binanceSymbol.getBaseAsset());
        pairCurrency.setQuoteCurrency(binanceSymbol.getQuoteAsset());
        pairCurrency.setQuantityScale(binanceSymbol.getBaseAssetPrecision());
        pairCurrency.setAmountScale(binanceSymbol.getQuotePrecision());
        pairCurrency.setExchangeToken(binanceSymbol.getSymbol());
        pairCurrency.setMarginTrading(binanceSymbol.isMarginTradingAllowed());
        binanceSymbol.getFilters().forEach(filter -> {
            if (filter.getFilterType().equals("LOT_SIZE")) {
                pairCurrency.setMinQuantity(filter.getMinQty());
            }
        });
        return pairCurrency;
    }

    public static PairCurrency fromHiBTCSymbol(HitBTCSymbol hitBTCSymbol) {
        PairCurrency pairCurrency = new PairCurrency();
        pairCurrency.setBaseCurrency(hitBTCSymbol.getBase_currency());
        pairCurrency.setQuoteCurrency(hitBTCSymbol.getQuote_currency());
        pairCurrency.setExchangeToken(hitBTCSymbol.getSymbol());
        pairCurrency.setMarginTrading(hitBTCSymbol.isMargin_trading());
        return pairCurrency;
    }
}
