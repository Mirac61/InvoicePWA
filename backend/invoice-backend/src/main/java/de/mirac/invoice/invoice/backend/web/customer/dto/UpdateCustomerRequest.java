package de.mirac.invoice.invoice.backend.web.customer.dto;

import de.mirac.invoice.invoice.backend.domain.customer.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerRequest(
    @NotNull CustomerType type,
    @NotBlank String forename,
    @NotBlank String surname,
    @NotBlank String street,
    @NotBlank String zip,
    @NotBlank String city,
    @NotBlank String country,
    @Email @NotBlank String email,
    @NotBlank String phone,
    String vatId
) {
}
