package de.mirac.invoice.invoice.backend.domain.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CustomerType type;

    @NotBlank
    @Column(name = "forename", nullable = false)
    private String forename;

    @NotBlank
    @Column(name = "surname", nullable = false)
    private String surname;

    @NotBlank
    @Column(name = "street")
    private String street;

    @NotBlank
    @Column(name = "zip")
    private String zip;

    @NotBlank
    @Column(name = "city")
    private String city;

    @NotBlank
    @Column(name = "country")
    private String country;

    @Email
    @NotBlank
    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "phone")
    private String phone;

    @Column(name = "vat_id")
    private String vatId;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    public Customer() {
    }

    public Customer(
            CustomerType type,
            String forename,
            String surname,
            String street,
            String zip,
            String city,
            String country,
            String email,
            String phone,
            String vatId
    ) {
        this.type = type;
        this.forename = forename;
        this.surname = surname;
        this.street = street;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.email = email;
        this.phone = phone;
        this.vatId = vatId;
    }

    @PrePersist
    void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        final String now = Instant.now().toString();
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

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
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

    // Compatibility: old code still uses 'name' field
    @Deprecated
    public String getName() {
        String fn = this.forename == null ? "" : this.forename.trim();
        String sn = this.surname == null ? "" : this.surname.trim();
        String full = (fn + " " + sn).trim();
        return full.isBlank() ? null : full;
    }

    @Deprecated
    public void setName(String name) {
        if (name == null) {
            this.forename = null;
            this.surname = null;
            return;
        }

        String trimmed = name.trim();
        if (trimmed.isBlank()) {
            this.forename = "";
            this.surname = "";
            return;
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            this.forename = parts[0];
            this.surname = "";
            return;
        }

        this.forename = parts[0];
        this.surname = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
    }

    // Compatibility: 'surename' typo exists in old code
    @Deprecated
    public String getSurename() {
        return getSurname();
    }

    @Deprecated
    public void setSurename(String surename) {
        setSurname(surename);
    }
}
