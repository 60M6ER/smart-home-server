package ru.bomber.trader.reposytory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bomber.trader.models.BalanceOperation;

import java.util.UUID;

@Repository
public interface BalanceOperationRepository extends JpaRepository<BalanceOperation, UUID> {

}
