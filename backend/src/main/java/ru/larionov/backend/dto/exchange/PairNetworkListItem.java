package ru.larionov.backend.dto.exchange;

import lombok.Data;
import ru.larionov.backend.dto.portfolio.NetworkListItem;

@Data
public class PairNetworkListItem {
    private NetworkListItem srcNetworkListItem;
    private NetworkListItem tgtNetworkListItem;
}
