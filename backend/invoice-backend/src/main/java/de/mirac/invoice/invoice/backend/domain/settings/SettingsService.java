package de.mirac.invoice.invoice.backend.domain.settings;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingsService {

    private static final String DEFAULT_ID = "default";
    private final SettingsRepository repo;

    public SettingsService(SettingsRepository repo) {
        this.repo = repo;
    }

    public Settings get() {
        return repo.findById(DEFAULT_ID).orElseGet(() -> repo.save(new Settings()));
    }

    public Settings update(Settings incoming) {
        Settings current = get();

        current.setCompanyName(incoming.getCompanyName());
        current.setContactName(incoming.getContactName());
        current.setAccountHolder(incoming.getAccountHolder());
        current.setRegisterCourt(incoming.getRegisterCourt());
        current.setRegisterNumber(incoming.getRegisterNumber());
        current.setOwner(incoming.getOwner());
        current.setManagingDirector(incoming.getManagingDirector());
        current.setLegalRepresentative(incoming.getLegalRepresentative());

        current.setStreet(incoming.getStreet());
        current.setHouseNumber(incoming.getHouseNumber());
        current.setZip(incoming.getZip());
        current.setCity(incoming.getCity());
        current.setCountry(incoming.getCountry());

        current.setEmail(incoming.getEmail());
        current.setPhone(incoming.getPhone());
        current.setWebsite(incoming.getWebsite());

        current.setTaxNumber(incoming.getTaxNumber());
        current.setVatId(incoming.getVatId());

        current.setIban(incoming.getIban());
        current.setBic(incoming.getBic());
        current.setBankName(incoming.getBankName());

        current.setInvoicePrefix(incoming.getInvoicePrefix());
        current.setNextInvoiceNo(incoming.getNextInvoiceNo());
        current.setPaymentDays(incoming.getPaymentDays());
        current.setDefaultVatRate(incoming.getDefaultVatRate());
        current.setInvoiceFooter(incoming.getInvoiceFooter());

        return repo.save(current);
    }
}
