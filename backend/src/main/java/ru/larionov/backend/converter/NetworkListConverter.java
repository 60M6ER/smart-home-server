package ru.larionov.backend.converter;

import ru.larionov.backend.dto.exchange.binance.BinanceNetworkListItem;
import ru.larionov.backend.dto.exchange.poloniex.PoloniexNetworkListItem;
import ru.larionov.backend.dto.portfolio.NetworkListItem;
import ru.larionov.backend.model.TypeDeposit;

public class NetworkListConverter {

    public static NetworkListItem fromPoloniex(PoloniexNetworkListItem networkListItem) {
        NetworkListItem newNetworkListItem = new NetworkListItem();
        newNetworkListItem.setNetworkToken(networkListItem.getCoin());
        newNetworkListItem.setBlockchainToken(networkListItem.getBlockchain());
        newNetworkListItem.setTypeDeposit(
                networkListItem.getCurrencyType().equals("address") ?
                        TypeDeposit.ADDRESS
                        : TypeDeposit.ADDRESS_PAYMENT_ID);
        newNetworkListItem.setDepositEnable(networkListItem.isDepositEnable());
        newNetworkListItem.setWithdrawalEnable(networkListItem.isWithdrawalEnable());
        newNetworkListItem.setWithdrawMin(networkListItem.getWithdrawMin());
        newNetworkListItem.setFee(networkListItem.getWithdrawFee());
        newNetworkListItem.setScale(getScale(networkListItem.getDecimals()));
        newNetworkListItem.setMinConfirm(networkListItem.getMinConfirm());
        return newNetworkListItem;
    }

    public static NetworkListItem fromBinance(BinanceNetworkListItem networkListItem) {
        NetworkListItem newNetworkListItem = new NetworkListItem();
        newNetworkListItem.setNetworkToken(networkListItem.getNetwork());
        newNetworkListItem.setBlockchainToken(networkListItem.getNetwork());
        newNetworkListItem.setTypeDeposit(
                !networkListItem.isSameAddress() ?
                        TypeDeposit.ADDRESS
                        : TypeDeposit.ADDRESS_PAYMENT_ID);
        newNetworkListItem.setDepositEnable(networkListItem.isDepositEnable());
        newNetworkListItem.setWithdrawalEnable(networkListItem.isWithdrawEnable());
        newNetworkListItem.setWithdrawMin(networkListItem.getWithdrawMin());
        newNetworkListItem.setFee(networkListItem.getWithdrawFee());
        newNetworkListItem.setScale(networkListItem.getWithdrawIntegerMultiple());
        newNetworkListItem.setMinConfirm(networkListItem.getMinConfirm());
        return newNetworkListItem;
    }

    private static double getScale(int a) {
        double c = 1;
        for (int i = 0; i < a; i++) {
            c /= 10;
        }
        return c;
    }
}
