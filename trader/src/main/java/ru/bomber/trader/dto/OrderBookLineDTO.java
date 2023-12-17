package ru.bomber.trader.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderBookLineDTO {

    private Double price;
    private Double quantity;

    public OrderBookLineDTO(Double price, Double quantity) {
        this.price = price;
        this.quantity = quantity;
    }
}
