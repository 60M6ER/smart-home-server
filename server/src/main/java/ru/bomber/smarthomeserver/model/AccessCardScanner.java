package ru.bomber.smarthomeserver.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "access_card_scanners")
public class AccessCardScanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "scanner_id")
    private String scannerId;

    @Column(name = "name")
    private String name;

    @Column(name = "work")
    private boolean work;
}
