package ru.larionov.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class Spread {
    private List<ChainPairs> chains;
    private List<OrderBook> orderBooks;
    private TypeSpread typeSpread;
    private int currentIndex;
    private SpreadState state;
    private double USDT_amount_start;
    private double USDT_amount_end;

    public Spread() {
        currentIndex = 0;
        state = SpreadState.CREATED;
    }

    public Spread(TypeSpread typeSpread) {
        this.typeSpread = typeSpread;
        currentIndex = 0;
        state = SpreadState.CREATED;
    }

    public void addChain(ChainPairs chainPair, OrderBook orderBook) {
        chains.add(chainPair);
        orderBooks.add(orderBook);
    }
}
