package ru.larionov.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.larionov.backend.model.Exchange;
import ru.larionov.backend.model.ExchangeVendor;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    Optional<Exchange> getExchangeByVendor(ExchangeVendor exchangeVendor);
    List<Exchange> findAllByActive(Boolean active);
}
