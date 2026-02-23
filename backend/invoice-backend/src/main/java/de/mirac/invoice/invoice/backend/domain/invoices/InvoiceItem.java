package de.mirac.invoice.invoice.backend.domain.invoices;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "unit")
    private String unit;

    @Column(name = "unit_price_net", nullable = false)
    private Double unitPriceNet;

    @Column(name = "tax_rate", nullable = false)
    private Double taxRate;

    @Column(name = "net_total", nullable = false)
    private Double netTotal;

    @Column(name = "tax_total", nullable = false)
    private Double taxTotal;

    @Column(name = "gross_total", nullable = false)
    private Double grossTotal;

    @PrePersist
    void onCreate() {
        if (id == null || id.isBlank()) {
            id = "invi_" + java.util.UUID.randomUUID();
        }

        if (netTotal == null) netTotal = 0.0;
        if (taxTotal == null) taxTotal = 0.0;
        if (grossTotal == null) grossTotal = 0.0;
    }

    public void recalcLineTotals() {
        double net = safe(quantity) * safe(unitPriceNet);
        double tax = net * safe(taxRate);

        netTotal = round2(net);
        taxTotal = round2(tax);
        grossTotal = round2(netTotal + taxTotal);
    }

    private double safe(Double v) {
        return v == null ? 0.0 : v;
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    public void setPosition(Integer position) { this.position = position; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setTitle(String title) { this.title = title; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setUnitPriceNet(Double unitPriceNet) { this.unitPriceNet = unitPriceNet; }
    public void setTaxRate(Double taxRate) { this.taxRate = taxRate; }

    public String getId() { return id; }
    public Invoice getInvoice() { return invoice; }
    public Integer getPosition() { return position; }
    public String getItemId() { return itemId; }
    public String getTitle() { return title; }
    public Double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public Double getUnitPriceNet() { return unitPriceNet; }
    public Double getTaxRate() { return taxRate; }
    public Double getNetTotal() { return netTotal; }
    public Double getTaxTotal() { return taxTotal; }
    public Double getGrossTotal() { return grossTotal; }
}
