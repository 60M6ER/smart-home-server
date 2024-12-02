package ru.bomber.trader.reposytory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.bomber.trader.models.Bot;
import java.util.UUID;

@Repository
public interface BotRepository extends JpaRepository<Bot, UUID>, JpaSpecificationExecutor<Bot> {

}
