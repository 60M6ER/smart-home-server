package ru.bomber.trader.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class Order {

    private UUID id;

    private String pair;

    private String exchangeId;

    private Double amount;

    private Double price;

    private RoleOrder roleOrder;

    private OrderOperation operation;

    private TypeOrder typeOrder;

    private Double fee;
}
