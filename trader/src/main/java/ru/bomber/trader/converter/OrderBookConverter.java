package ru.bomber.trader.converter;

import com.poloniex.api.client.model.OrderBook;
import ru.bomber.trader.dto.OrderBookDTO;
import ru.bomber.trader.dto.OrderBookLineDTO;

import java.util.ArrayList;
import java.util.List;

public class OrderBookConverter {

    public static OrderBookDTO toDTO(OrderBook orderBook) {
        OrderBookDTO orderBookDTO = new OrderBookDTO();

        List<OrderBookLineDTO> ask = new ArrayList<>();
        List<OrderBookLineDTO> bid = new ArrayList<>();

        fillList(ask, orderBook.getAsks());
        fillList(bid, orderBook.getBids());

        return orderBookDTO;
    }

    private static void fillList(List<OrderBookLineDTO> receiver, List<String> source) {
        for (int i = 0; i < source.size(); i+=2) {
            receiver.add(new OrderBookLineDTO(Double.parseDouble(source.get(i)), Double.parseDouble(source.get(i + 1))));
        }
    }
}
