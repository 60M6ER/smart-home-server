package ru.larionov.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "currencies")
public class Currency {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "name")
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "vendor")
    private ExchangeVendor vendor;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "hold_amount")
    private Double holdAmount;
    @Column(name = "usd_equal")
    private Double usdEqual;
    @Column(name = "address")
    private String address;
}
