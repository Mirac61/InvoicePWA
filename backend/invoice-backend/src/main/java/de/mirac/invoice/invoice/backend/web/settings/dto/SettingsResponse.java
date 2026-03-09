package de.mirac.invoice.invoice.backend.web.settings.dto;

import de.mirac.invoice.invoice.backend.domain.settings.Settings;

public record SettingsResponse(
    String id,

    String companyName,
    String contactName,
    String accountHolder,
    String registerCourt,
    String registerNumber,
    String owner,
    String managingDirector,
    String legalRepresentative,

    String street,
    String houseNumber,
    String zip,
    String city,
    String country,

    String email,
    String phone,
    String website,

    String taxNumber,
    String vatId,

    String iban,
    String bic,
    String bankName,

    String invoicePrefix,
    Integer nextInvoiceNo,
    Integer paymentDays,
    Double defaultVatRate,

    String invoiceFooter,

    String createdAt,
    String updatedAt
) {
    public static SettingsResponse from(Settings s) {
        return new SettingsResponse(
            s.getId(),

            s.getCompanyName(),
            s.getContactName(),
            s.getAccountHolder(),
            s.getRegisterCourt(),
            s.getRegisterNumber(),
            s.getOwner(),
            s.getManagingDirector(),
            s.getLegalRepresentative(),

            s.getStreet(),
            s.getHouseNumber(),
            s.getZip(),
            s.getCity(),
            s.getCountry(),

            s.getEmail(),
            s.getPhone(),
            s.getWebsite(),

            s.getTaxNumber(),
            s.getVatId(),

            s.getIban(),
            s.getBic(),
            s.getBankName(),

            s.getInvoicePrefix(),
            s.getNextInvoiceNo(),
            s.getPaymentDays(),
            s.getDefaultVatRate(),

            s.getInvoiceFooter(),

            s.getCreatedAt(),
            s.getUpdatedAt()
        );
    }
}
