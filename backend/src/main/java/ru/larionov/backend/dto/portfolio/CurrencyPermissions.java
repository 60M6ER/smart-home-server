package ru.larionov.backend.dto.portfolio;

import lombok.Data;

import java.util.List;

@Data
public class CurrencyPermissions {
    private String token;
    private boolean tradeEnable;
    private boolean supportBorrow;
    private List<NetworkListItem> networkList;
}
