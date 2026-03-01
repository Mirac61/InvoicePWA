package de.mirac.invoice.invoice.backend.web.items.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateItemRequest(
    @NotBlank String name,
    String description,
    String defaultUnit,
    Double defaultTaxRate,
    @NotNull Double unitPriceNet,
    Boolean active
) {}
