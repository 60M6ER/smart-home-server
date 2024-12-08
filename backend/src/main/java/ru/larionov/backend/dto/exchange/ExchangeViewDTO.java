package ru.larionov.backend.dto.exchange;

import lombok.Data;
import ru.larionov.backend.model.ExchangeVendor;

@Data
public class ExchangeViewDTO {
    private Long id;
    private String name;
    private ExchangeVendor vendor;
}
