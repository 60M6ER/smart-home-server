package ru.bomber.trader.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Subselect("SELECT\n" +
        "    bo.bot_id AS id,\n" +
        "    bo.instrument AS instrument,\n" +
        "    sum(bo.amount) AS amount\n" +
        "FROM balance_operations bo\n" +
        "GROUP BY bo.bot_id, bo.instrument")
@NoArgsConstructor
@Setter
@Getter
public class Balance {

    @Id
    private UUID id;

    private String instrument;

    private Double amount;
}
