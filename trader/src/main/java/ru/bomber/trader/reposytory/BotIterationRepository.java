package ru.bomber.trader.reposytory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bomber.trader.models.BotIteration;

import java.util.UUID;

@Repository
public interface BotIterationRepository extends JpaRepository<BotIteration, UUID> {
}
