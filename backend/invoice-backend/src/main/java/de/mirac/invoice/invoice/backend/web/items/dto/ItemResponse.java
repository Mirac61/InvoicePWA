package de.mirac.invoice.invoice.backend.web.items.dto;

import de.mirac.invoice.invoice.backend.domain.items.Item;

public record ItemResponse(
    String id,
    String name,
    String description,
    String defaultUnit,
    Double defaultTaxRate,
    Double unitPriceNet,
    Boolean active,
    String createdAt,
    String updatedAt
) {
    public static ItemResponse from(Item i) {
        return new ItemResponse(
            i.getId(),
            i.getName(),
            i.getDescription(),
            i.getDefaultUnit(),
            i.getDefaultTaxRate(),
            i.getUnitPriceNet(),
            i.getActive(),
            i.getCreatedAt(),
            i.getUpdatedAt()
        );
    }
}
