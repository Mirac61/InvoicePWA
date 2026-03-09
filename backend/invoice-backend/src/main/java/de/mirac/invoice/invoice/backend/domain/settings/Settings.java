package de.mirac.invoice.invoice.backend.domain.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "settings")
public class Settings {

    // Firma
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @NotBlank
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "account_holder")
    private String accountHolder;

    @Column(name = "register_court")
    private String registerCourt;

    @Column(name = "register_number")
    private String registerNumber;

    @Column(name = "owner")
    private String owner;

    @Column(name = "managing_director")
    private String managingDirector;

    @Column(name = "legal_representative")
    private String legalRepresentative;

    // Adresse
    @NotBlank
    @Column(name = "street", nullable = false)
    private String street;

    @NotBlank
    @Column(name = "house_number", nullable = false)
    private String houseNumber;

    @NotBlank
    @Column(name = "zip", nullable = false)
    private String zip;

    @NotBlank
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank
    @Column(name = "country", nullable = false)
    private String country;

    // Kontakt
    @Email
    @NotBlank
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank
    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "website")
    private String website;

    // Steuern
    @Column(name = "tax_number")
    private String taxNumber;

    @Column(name = "vat_id")
    private String vatId;

    // Bank
    @Column(name = "iban")
    private String iban;

    @Column(name = "bic")
    private String bic;

    @Column(name = "bank_name")
    private String bankName;

    // Rechnung Standards
    @NotBlank
    @Column(name = "invoice_prefix", nullable = false)
    private String invoicePrefix = "INV";

    @jakarta.validation.constraints.Positive
    @Column(name = "next_invoice_no", nullable = false)
    private Integer nextInvoiceNo = 1;

    @jakarta.validation.constraints.Positive
    @Column(name = "payment_days", nullable = false)
    private Integer paymentDays = 14;

    // Steuern Standard 19%
    @jakarta.validation.constraints.DecimalMin("0.0")
    @jakarta.validation.constraints.DecimalMax("1.0")
    @Column(name = "default_vat_rate", nullable = false)
    private Double defaultVatRate = 0.19;

    // Texte für PDF
    @Column(name = "invoice_footer", length = 2000)
    private String invoiceFooter;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    public Settings() {
    }

    public Settings(
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
            String phone
    ) {
        this.companyName = companyName;
        this.contactName = contactName;
        this.accountHolder = accountHolder;
        this.registerCourt = registerCourt;
        this.registerNumber = registerNumber;
        this.owner = owner;
        this.managingDirector = managingDirector;
        this.legalRepresentative = legalRepresentative;
        this.street = street;
        this.houseNumber = houseNumber;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.email = email;
        this.phone = phone;
    }

    @PrePersist
    void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = "default";
        }

        String now = Instant.now().toString();

        if (this.createdAt == null || this.createdAt.isBlank()) {
            this.createdAt = now;
        }

        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now().toString();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getInvoicePrefix() {
        return invoicePrefix;
    }

    public void setInvoicePrefix(String invoicePrefix) {
        this.invoicePrefix = invoicePrefix;
    }

    public Integer getNextInvoiceNo() {
        return nextInvoiceNo;
    }

    public void setNextInvoiceNo(Integer nextInvoiceNo) {
        this.nextInvoiceNo = nextInvoiceNo;
    }

    public Integer getPaymentDays() {
        return paymentDays;
    }

    public void setPaymentDays(Integer paymentDays) {
        this.paymentDays = paymentDays;
    }

    public Double getDefaultVatRate() {
        return defaultVatRate;
    }

    public void setDefaultVatRate(Double defaultVatRate) {
        this.defaultVatRate = defaultVatRate;
    }

    public String getInvoiceFooter() {
        return invoiceFooter;
    }

    public void setInvoiceFooter(String invoiceFooter) {
        this.invoiceFooter = invoiceFooter;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getRegisterCourt() {
        return registerCourt;
    }

    public void setRegisterCourt(String registerCourt) {
        this.registerCourt = registerCourt;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getManagingDirector() {
        return managingDirector;
    }

    public void setManagingDirector(String managingDirector) {
        this.managingDirector = managingDirector;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }
}
