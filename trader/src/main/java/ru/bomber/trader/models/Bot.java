package ru.bomber.trader.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.bomber.core.trader.models.ExchangeVendor;
import ru.bomber.core.trader.models.StrategyType;

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

    @Column(name = "base_instrument")
    private String baseInstrument;

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

    @Column(name = "strategy_type")
    @Enumerated(EnumType.ORDINAL)
    private StrategyType strategyType;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "exchange_vendor")
    @Enumerated(EnumType.ORDINAL)
    private ExchangeVendor vendor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bot bot = (Bot) o;

        return id.equals(bot.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
