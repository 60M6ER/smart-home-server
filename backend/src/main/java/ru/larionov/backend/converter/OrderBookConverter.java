package ru.larionov.backend.converter;

import ru.larionov.backend.model.OrderBook;
import ru.larionov.backend.model.OrderBookRow;

import java.util.ArrayList;
import java.util.List;

public class OrderBookConverter {

    public static OrderBook fromPoloniexOrderBook(com.poloniex.api.client.spot.model.response.spot.OrderBook pOrderBook,
                                                  String pairToken) {
        OrderBook orderBook = new OrderBook();
        orderBook.setPairToken(pairToken);
        orderBook.setPriceScale(Double.parseDouble(pOrderBook.getScale()));
        orderBook.setBids(toOrderBookRow(pOrderBook.getBids()));
        orderBook.setAsks(toOrderBookRow(pOrderBook.getAsks()));
        return orderBook;
    }

    private static List<OrderBookRow> toOrderBookRow(List<String> rows) {
        List<OrderBookRow> orderBookRows = new ArrayList<>();
        OrderBookRow bookRow = null;
        for (int i = 0; i < rows.size(); i++) {
            if ((i & 1) == 0) {
                bookRow = new OrderBookRow();
                bookRow.setPrice(Double.parseDouble(rows.get(i)));
            } else {
                bookRow.setAmount(Double.parseDouble(rows.get(i)));
                orderBookRows.add(bookRow);
            }
        }
        return orderBookRows;
    }
}
