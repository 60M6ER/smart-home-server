package ru.bomber.trader.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "bots")
@Setter
@Getter
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "pair")
    private String pair;

    @Column(name = "fiat")
    private String fiat;

    @Column(name = "min_amount")
    private Double minAmount;

    @Column(name = "amount_at_percent")
    private Boolean amountAtPercent;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "step_to_bay")
    private Double stepToBay;

    @Column(name = "step_at_percent")
    private Boolean stepAtPercent;

    @Column(name = "equal_steps")
    private Boolean equalSteps;
}
