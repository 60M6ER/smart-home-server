package ru.larionov.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "portfolio_balances")
public class PortfolioBalance {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "usd")
    private Double usd;
    @Column(name = "rub")
    private Double rub;
    @Column(name = "date")
    private Date date;
}
