package de.mirac.invoice.invoice.backend.web.invoice.dto;

import de.mirac.invoice.invoice.backend.web.customer.dto.CustomerResponse;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceResponse {

    private String id;
    private String invoiceNumber;
    private CustomerResponse customer;
    private String issueDate;
    private String serviceDate;
    private String currency;
    private String notes;
    private Double netTotal;
    private Double taxTotal;
    private Double grossTotal;
    private String status;

    /** All line items belonging to this invoice, ordered by position ascending. */
    private List<InvoiceItemResponse> items;

    private String createdAt;
    private String updatedAt;
}
