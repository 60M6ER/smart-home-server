package ru.larionov.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.larionov.backend.dto.exchange.PairNetworkListItem;
import ru.larionov.backend.dto.portfolio.CurrencyKey;
import ru.larionov.backend.dto.portfolio.CurrencyPermissions;
import ru.larionov.backend.dto.portfolio.NetworkListItem;
import ru.larionov.backend.exception.NetworksAreNotEquals;
import ru.larionov.backend.model.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private static final long TIME_TO_LIVE_CURRENCY_PERMISSIONS = 1000 * 60 * 60 * 5; // 5 hours

    private final ExchangeHandlerService exchangeHandlerService;

    private HashMap<CurrencyKey, CurrencyPermissions> permissionsHashMap;


    @PostConstruct
    private void initLogic() {
        permissionsHashMap = new HashMap<>();
    }

    public CurrencyPermissions getCurrencyPermissions(String currencyToken, ExchangeVendor vendor) {
        CurrencyKey key = new CurrencyKey(currencyToken, vendor);
        CurrencyPermissions currencyPermissions = null;
        boolean needUpdate = true;
        if (permissionsHashMap.containsKey(key)) {
            currencyPermissions = permissionsHashMap.get(key);
            needUpdate = currencyPermissions.getTimestamp() + TIME_TO_LIVE_CURRENCY_PERMISSIONS < new Date().getTime();
        }
        if (needUpdate) {
            currencyPermissions = exchangeHandlerService.getCurrencyPermissions(currencyToken, vendor);
            permissionsHashMap.put(key, currencyPermissions);
        }
        return currencyPermissions;
    }

    public double getFeeWithdrawal(CurrencyPermissions srcCurrencyPermissions,
                                   CurrencyPermissions targetCurrencyPermissions) throws NetworksAreNotEquals {
        double fee = -2;
        PairNetworkListItem pair = getPairNetworkListItem(srcCurrencyPermissions, targetCurrencyPermissions);
        fee = pair.getSrcNetworkListItem().getFee();

        return fee;
    }

    private PairNetworkListItem getPairNetworkListItem(CurrencyPermissions srcCurrencyPermissions,
                                                       CurrencyPermissions targetCurrencyPermissions) throws NetworksAreNotEquals {
        List<NetworkListItem> srcNetworkListItems = getSortedNetworkListItems(srcCurrencyPermissions.getNetworkList(), true);
        List<NetworkListItem> targetNetworkListItems = getSortedNetworkListItems(targetCurrencyPermissions.getNetworkList(), false);
        PairNetworkListItem pair = new PairNetworkListItem();
        for (NetworkListItem src: srcNetworkListItems) {
            for (NetworkListItem target: targetNetworkListItems) {
                if (src.getBlockchainToken().equals(target.getBlockchainToken())) {
                    pair.setSrcNetworkListItem(src);
                    pair.setTgtNetworkListItem(target);
                    break;
                }
            }
            if (pair.getTgtNetworkListItem() != null && pair.getTgtNetworkListItem() != null) {
                break;
            }
        }
        if (pair.getSrcNetworkListItem() == null || pair.getTgtNetworkListItem() == null)
            throw new NetworksAreNotEquals("There aren't equal networks: "
                    + srcCurrencyPermissions.getToken()
                    + " - "
                    + targetCurrencyPermissions.getToken());
        return pair;
    }

    private List<NetworkListItem> getSortedNetworkListItems(List<NetworkListItem> list, boolean isWithdraw) {
        return list.stream()
                .filter(item -> isWithdraw ? item.isWithdrawalEnable() : item.isDepositEnable() &&
                        item.getTypeDeposit() == TypeDeposit.ADDRESS)
                .sorted((o1, o2) -> {
                    double v = o1.getFee() - o2.getFee();
                    int i = o1.getMinConfirm() - o2.getMinConfirm();
                    return (int) (v * 1000) + i;
                })
                .toList();
    }

    private UUID createWithdraw(String currencyToken,
                                ExchangeVendor srcVendor,
                                ExchangeVendor tgtVendor,
                                double amount) throws NetworksAreNotEquals {
        CurrencyPermissions srcCurPermissions = getCurrencyPermissions(currencyToken, srcVendor);
        CurrencyPermissions tgtCurPermissions = getCurrencyPermissions(currencyToken, tgtVendor);
        PairNetworkListItem pairNetworkListItem = getPairNetworkListItem(srcCurPermissions, tgtCurPermissions);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(UUID.randomUUID());
        double factAmount = ((int)(amount / pairNetworkListItem.getSrcNetworkListItem().getScale()))
                * pairNetworkListItem.getSrcNetworkListItem().getScale();
        withdrawal.setAmount(factAmount);
        withdrawal.setState(WithdrawalState.CREATED);
        withdrawal.setFee(pairNetworkListItem.getSrcNetworkListItem().getFee());
        withdrawal.setSrcVendor(srcVendor);
        withdrawal.setTargetVendor(tgtVendor);
        DepositAddress depositAddress = exchangeHandlerService.getDepositAddress(pairNetworkListItem.getTgtNetworkListItem().getNetworkToken(),
                pairNetworkListItem.getTgtNetworkListItem().getBlockchainToken(),
                tgtVendor);
        withdrawal.setAddress(depositAddress.getAddress());
        return withdrawal.getId();
    }
}
