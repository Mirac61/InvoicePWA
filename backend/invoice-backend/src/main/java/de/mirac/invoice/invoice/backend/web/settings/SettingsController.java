package de.mirac.invoice.invoice.backend.web.settings;

import de.mirac.invoice.invoice.backend.domain.settings.Settings;
import de.mirac.invoice.invoice.backend.domain.settings.SettingsService;
import de.mirac.invoice.invoice.backend.web.settings.dto.SettingsResponse;
import de.mirac.invoice.invoice.backend.web.settings.dto.UpdateSettingsRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService service;

    public SettingsController(SettingsService service) {
        this.service = service;
    }

    @GetMapping
    public SettingsResponse get() {
        return SettingsResponse.from(service.get());
    }

    @PutMapping
    public SettingsResponse update(@RequestBody UpdateSettingsRequest req) {
        Settings incoming = new Settings();

        incoming.setCompanyName(req.companyName());
        incoming.setContactName(req.contactName());
        incoming.setAccountHolder(req.accountHolder());
        incoming.setRegisterCourt(req.registerCourt());
        incoming.setRegisterNumber(req.registerNumber());
        incoming.setOwner(req.owner());
        incoming.setManagingDirector(req.managingDirector());
        incoming.setLegalRepresentative(req.legalRepresentative());

        incoming.setStreet(req.street());
        incoming.setHouseNumber(req.houseNumber());
        incoming.setZip(req.zip());
        incoming.setCity(req.city());
        incoming.setCountry(req.country());

        incoming.setEmail(req.email());
        incoming.setPhone(req.phone());
        incoming.setWebsite(req.website());

        incoming.setTaxNumber(req.taxNumber());
        incoming.setVatId(req.vatId());

        incoming.setIban(req.iban());
        incoming.setBic(req.bic());
        incoming.setBankName(req.bankName());

        incoming.setInvoicePrefix(req.invoicePrefix());
        incoming.setNextInvoiceNo(req.nextInvoiceNo());
        incoming.setPaymentDays(req.paymentDays());
        incoming.setDefaultVatRate(req.defaultVatRate());

        incoming.setInvoiceFooter(req.invoiceFooter());

        return SettingsResponse.from(service.update(incoming));
    }
}
