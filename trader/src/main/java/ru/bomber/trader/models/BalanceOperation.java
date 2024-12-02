package ru.bomber.trader.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@Table(name = "balance_operations")
public class BalanceOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "instrument")
    private String instrument;

    @Column(name = "source")
    @Enumerated(EnumType.ORDINAL)
    private OperationSource source;

    @Column(name = "amount")
    private Double amount;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(name = "iteration")
    private UUID iteration;
}
