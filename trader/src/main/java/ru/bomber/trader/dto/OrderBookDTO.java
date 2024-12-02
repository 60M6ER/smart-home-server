package ru.bomber.trader.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookDTO {

    private List<OrderBookLineDTO> ask;
    private List<OrderBookLineDTO> bid;
}
