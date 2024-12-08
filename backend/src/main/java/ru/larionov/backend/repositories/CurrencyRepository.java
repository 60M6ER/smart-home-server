package ru.larionov.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.larionov.backend.model.Currency;
import ru.larionov.backend.model.ExchangeVendor;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {

    Optional<Currency> findByNameAndVendor(String name, ExchangeVendor vendor);
}
