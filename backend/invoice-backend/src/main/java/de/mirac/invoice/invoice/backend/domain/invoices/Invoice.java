package de.mirac.invoice.invoice.backend.domain.invoices;

import de.mirac.invoice.invoice.backend.domain.customer.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "invoice_date", nullable = false)
    private String issueDate;

    @Column(name = "service_date")
    private String serviceDate;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "notes")
    private String notes;

    @Column(name = "net_total", nullable = false)
    private Double netTotal;

    @Column(name = "tax_total", nullable = false)
    private Double taxTotal;

    @Column(name = "gross_total", nullable = false)
    private Double grossTotal;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<InvoiceItem> items = new ArrayList<>();

    public void setItems(List<InvoiceItem> newItems) {
        items.clear();
        if (newItems == null) {
            recalcTotals();
            return;
        }
        for (InvoiceItem it : newItems) {
            it.setInvoice(this);
            items.add(it);
        }
        recalcTotals();
    }

    public void recalcTotals() {
        double net = 0.0;
        double tax = 0.0;
        double gross = 0.0;

        for (InvoiceItem it : items) {
            it.recalcLineTotals();
            net += safe(it.getNetTotal());
            tax += safe(it.getTaxTotal());
            gross += safe(it.getGrossTotal());
        }

        netTotal = round2(net);
        taxTotal = round2(tax);
        grossTotal = round2(gross);
    }

    private double safe(Double v) {
        return v == null ? 0.0 : v;
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
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

        if (this.netTotal == null) this.netTotal = 0.0;
        if (this.taxTotal == null) this.taxTotal = 0.0;
        if (this.grossTotal == null) this.grossTotal = 0.0;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now().toString();
    }
}
