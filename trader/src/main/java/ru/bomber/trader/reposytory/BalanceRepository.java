package ru.bomber.trader.reposytory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bomber.trader.view.Balance;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {

    Optional<Balance> findByIdAndInstrument(UUID id, String instrument);
}
