package ru.bomber.smarthomeserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "access_cards")
public class AccessCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "card_id")
    private long cardId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(name = "work")
    private boolean work;
}
