package ru.larionov.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "withdrawals")
public class Withdrawal {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "exchange_id")
    private String exchangeId;
    @Column(name = "amount")
    private double amount;
    @Column(name = "fee")
    private double fee;
    @Column(name = "src_vendor")
    private ExchangeVendor srcVendor;
    @Column(name = "target_vendor")
    private ExchangeVendor targetVendor;
    @Column(name = "address")
    private String address;
    @Column(name = "state")
    private WithdrawalState state;
}
