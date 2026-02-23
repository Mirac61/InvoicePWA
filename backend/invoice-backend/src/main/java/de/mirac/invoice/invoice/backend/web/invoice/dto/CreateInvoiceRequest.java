package de.mirac.invoice.invoice.backend.web.invoice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateInvoiceRequest {
    private String customerId;
    private String issueDate;
    private String serviceDate;
    private String currency;
    private String notes;
    private String status;

    @NotNull
    private List<CreateInvoiceItemRequest> items;
}
