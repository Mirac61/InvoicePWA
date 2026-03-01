package de.mirac.invoice.invoice.backend.web.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

/**
 * Request payload for a single invoice line item.
 *
 * <p><b>taxRate convention:</b> pass the tax rate as a decimal fraction.
 * <ul>
 *   <li>19 % → {@code 0.19}</li>
 *   <li>7 %  → {@code 0.07}</li>
 *   <li>0 %  → {@code 0.0}</li>
 * </ul>
 *
 * <p><b>unit:</b> defaults to {@code "pcs"} when omitted or blank.
 */
public record CreateInvoiceItemRequest(

        @NotNull
        Integer position,


        String itemId,

        @NotBlank
        String title,

        @NotNull @Positive
        Double quantity,


        String unit,

        @NotNull @PositiveOrZero
        Double unitPriceNet,

        /**
         * Tax rate as a decimal fraction (0.19 = 19 %).
         * Must be >= 0.
         */
        @NotNull @PositiveOrZero
        Double taxRate

) {}
