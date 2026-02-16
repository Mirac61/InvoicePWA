package de.mirac.invoice.invoice.backend.domain.items;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents an item or service that can be included in an invoice.
 */
@Entity
@Table(name = "items")
public class Item {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "default_unit")
    private String defaultUnit;

    @NotNull
    @Column(name = "default_tax_rate", nullable = false)
    private Double defaultTaxRate;

    @NotNull
    @Column(name = "unit_price_net", nullable = false)
    private Double unitPriceNet;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    public Item() {}

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

        if (this.active == null) {
            this.active = true;
        }
        if (this.defaultTaxRate == null) {
            this.defaultTaxRate = 0.0;
        }
        if (this.unitPriceNet == null) {
            this.unitPriceNet = 0.0;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now().toString();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDefaultUnit() { return defaultUnit; }
    public void setDefaultUnit(String defaultUnit) { this.defaultUnit = defaultUnit; }

    public Double getDefaultTaxRate() { return defaultTaxRate; }
    public void setDefaultTaxRate(Double defaultTaxRate) { this.defaultTaxRate = defaultTaxRate; }

    public Double getUnitPriceNet() { return unitPriceNet; }
    public void setUnitPriceNet(Double unitPriceNet) { this.unitPriceNet = unitPriceNet; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
