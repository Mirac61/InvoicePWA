package de.mirac.invoice.invoice.backend.web.customer.dto;

import de.mirac.invoice.invoice.backend.domain.customer.CustomerType;

public record CustomerResponse(
    String id,
    CustomerType type,
    String name,
    String street,
    String zip,
    String city,
    String country,
    String email,
    String phone,
    String vatId,
    String createdAt,
    String updatedAt
) {
}
