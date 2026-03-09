package de.mirac.invoice.invoice.backend.web.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponse {

    private String id;
    private Integer position;

    private String itemId;

    private String title;
    private Double quantity;
    private String unit;

    private Double unitPriceNet;

    /**
     * Tax rate as a decimal fraction.
     * Examples: 0.19 = 19 %, 0.07 = 7 %, 0.0 = 0 %
     */
    private Double taxRate;
    private Double netTotal;
    private Double taxTotal;
    private Double grossTotal;
}
