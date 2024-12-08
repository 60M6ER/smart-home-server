package ru.larionov.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "exchanges")
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "api_key")
    private String API_KEY;
    @Column(name = "api_secret")
    private String SECRET;
    @Enumerated(EnumType.STRING)
    @Column(name = "vendor")
    private ExchangeVendor vendor;
    @Column(name = "active")
    private Boolean active;
}
