package ru.bomber.trader.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "bot_iterations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BotIteration {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "iteration_id")
    private UUID iterationId;
}
