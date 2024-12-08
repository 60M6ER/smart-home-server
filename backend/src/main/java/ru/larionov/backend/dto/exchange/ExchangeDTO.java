package ru.larionov.backend.dto.exchange;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import ru.larionov.backend.model.ExchangeVendor;

@Data
public class ExchangeDTO {
    private Long id;
    private String name;
    private ExchangeVendor vendor;
    private String apiKey;
    private String secret;
    private Boolean active;
}
