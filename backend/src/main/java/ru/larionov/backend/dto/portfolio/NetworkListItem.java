package ru.larionov.backend.dto.portfolio;

import lombok.Data;
import ru.larionov.backend.model.TypeDeposit;

@Data
public class NetworkListItem {
    private String networkToken;
    private String blockchainToken;
    private TypeDeposit typeDeposit;
    private boolean depositEnable;
    private boolean withdrawalEnable;
    private double withdrawMin;
    private double fee;
    private double scale;
    private int minConfirm;
}
