package ru.larionov.backend.converter;

import ru.larionov.backend.dto.exchange.binance.BinanceCurrencyInfo;
import ru.larionov.backend.dto.exchange.poloniex.PoloniexCurrencyInformation;
import ru.larionov.backend.dto.portfolio.CurrencyPermissions;

import java.util.Date;

public class CurrencyPermissionsConverter {

    public static CurrencyPermissions fromPoloniex(PoloniexCurrencyInformation currencyInformation) {
        CurrencyPermissions currencyPermissions = new CurrencyPermissions();
        currencyPermissions.setToken(currencyInformation.getCoin());
        currencyPermissions.setTradeEnable(currencyInformation.isTradeEnable());
        currencyPermissions.setSupportBorrow(currencyInformation.isSupportBorrow());
        currencyPermissions.setNetworkList(
                currencyInformation.getNetworkList().stream()
                        .map(NetworkListConverter::fromPoloniex)
                        .toList()
        );
        currencyPermissions.setTimestamp(new Date().getTime());
        return currencyPermissions;
    }

    public static CurrencyPermissions fromBinance(BinanceCurrencyInfo currencyInfo) {
        CurrencyPermissions currencyPermissions = new CurrencyPermissions();
        currencyPermissions.setToken(currencyInfo.getCoin());
        currencyPermissions.setTradeEnable(currencyInfo.isTrading());
        currencyPermissions.setNetworkList(
                currencyInfo.getNetworkList().stream()
                        .map(NetworkListConverter::fromBinance)
                        .toList()
        );
        currencyPermissions.setTimestamp(new Date().getTime());
        return currencyPermissions;
    }
}
