package de.mirac.invoice.invoice.backend.web.settings.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateSettingsRequest(
    @NotBlank String companyName,
    String contactName,
    String accountHolder,
    String registerCourt,
    String registerNumber,
    String owner,
    String managingDirector,
    String legalRepresentative,

    @NotBlank String street,
    @NotBlank String houseNumber,
    @NotBlank String zip,
    @NotBlank String city,
    @NotBlank String country,

    @Email @NotBlank String email,
    @NotBlank String phone,
    String website,

    String taxNumber,
    String vatId,

    String iban,
    String bic,
    String bankName,

    @NotBlank String invoicePrefix,
    @NotNull @Positive Integer nextInvoiceNo,
    @NotNull @Positive Integer paymentDays,

    @NotNull @DecimalMin("0.0") @DecimalMax("1.0") Double defaultVatRate,

    String invoiceFooter
) {}
