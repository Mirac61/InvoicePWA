package de.mirac.invoice.invoice.backend.web.invoice;

import de.mirac.invoice.invoice.backend.domain.customer.Customer;
import de.mirac.invoice.invoice.backend.domain.invoices.Invoice;
import de.mirac.invoice.invoice.backend.domain.invoices.InvoiceItem;
import de.mirac.invoice.invoice.backend.domain.invoices.InvoiceService;
import de.mirac.invoice.invoice.backend.web.customer.dto.CustomerResponse;
import de.mirac.invoice.invoice.backend.web.invoice.dto.CreateInvoiceItemRequest;
import de.mirac.invoice.invoice.backend.web.invoice.dto.CreateInvoiceRequest;
import de.mirac.invoice.invoice.backend.web.invoice.dto.InvoiceItemResponse;
import de.mirac.invoice.invoice.backend.web.invoice.dto.InvoiceResponse;
import de.mirac.invoice.invoice.backend.web.invoice.dto.UpdateInvoiceRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        List<InvoiceResponse> invoices = invoiceService.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable String id) {
        return invoiceService.findById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        Invoice invoice = convertToEntity(request);
        Invoice savedInvoice = invoiceService.save(invoice);
        return new ResponseEntity<>(convertToDto(savedInvoice), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable String id,
            @Valid @RequestBody UpdateInvoiceRequest request
    ) {
        Invoice updatedInvoice = convertToEntity(id, request);
        Invoice savedInvoice = invoiceService.update(id, updatedInvoice);
        return ResponseEntity.ok(convertToDto(savedInvoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable String id) {
        invoiceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    private InvoiceResponse convertToDto(Invoice invoice) {
        InvoiceResponse dto = new InvoiceResponse();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());

        if (invoice.getCustomer() != null) {
            Customer c = invoice.getCustomer();
            dto.setCustomer(new CustomerResponse(
                    c.getId(),
                    c.getType(),
                    c.getForename(),
                    c.getSurname(),
                    c.getStreet(),
                    c.getZip(),
                    c.getCity(),
                    c.getCountry(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getVatId(),
                    c.getCreatedAt(),
                    c.getUpdatedAt()
            ));
        }

        dto.setIssueDate(invoice.getIssueDate());
        dto.setServiceDate(invoice.getServiceDate());
        dto.setCurrency(invoice.getCurrency());
        dto.setNotes(invoice.getNotes());
        dto.setNetTotal(invoice.getNetTotal());
        dto.setTaxTotal(invoice.getTaxTotal());
        dto.setGrossTotal(invoice.getGrossTotal());
        dto.setStatus(invoice.getStatus());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setUpdatedAt(invoice.getUpdatedAt());

        // Map line items — previously missing, now included
        if (invoice.getItems() != null) {
            List<InvoiceItemResponse> itemDtos = invoice.getItems().stream()
                    .map(this::convertItemToDto)
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
        } else {
            dto.setItems(new ArrayList<>());
        }

        return dto;
    }

    private InvoiceItemResponse convertItemToDto(InvoiceItem item) {
        InvoiceItemResponse dto = new InvoiceItemResponse();
        dto.setId(item.getId());
        dto.setPosition(item.getPosition());
        dto.setItemId(item.getItemId());
        dto.setTitle(item.getTitle());
        dto.setQuantity(item.getQuantity());
        dto.setUnit(item.getUnit());
        dto.setUnitPriceNet(item.getUnitPriceNet());
        dto.setTaxRate(item.getTaxRate());
        dto.setNetTotal(item.getNetTotal());
        dto.setTaxTotal(item.getTaxTotal());
        dto.setGrossTotal(item.getGrossTotal());
        return dto;
    }

    private Invoice convertToEntity(CreateInvoiceRequest request) {
        Invoice invoice = new Invoice();

        Customer customer = new Customer();
        customer.setId(request.getCustomerId());
        invoice.setCustomer(customer);

        invoice.setIssueDate(request.getIssueDate());
        invoice.setServiceDate(request.getServiceDate());
        invoice.setCurrency(request.getCurrency());
        invoice.setNotes(request.getNotes());
        invoice.setStatus(request.getStatus());
        invoice.setItems(mapItems(request.getItems()));

        return invoice;
    }

    private Invoice convertToEntity(String id, UpdateInvoiceRequest request) {
        Invoice invoice = new Invoice();
        invoice.setId(id);

        Customer customer = new Customer();
        customer.setId(request.getCustomerId());
        invoice.setCustomer(customer);

        invoice.setIssueDate(request.getIssueDate());
        invoice.setServiceDate(request.getServiceDate());
        invoice.setCurrency(request.getCurrency());
        invoice.setNotes(request.getNotes());
        invoice.setStatus(request.getStatus());
        invoice.setItems(mapItems(request.getItems()));

        return invoice;
    }

    private List<InvoiceItem> mapItems(List<CreateInvoiceItemRequest> reqItems) {
        List<InvoiceItem> out = new ArrayList<>();
        for (CreateInvoiceItemRequest r : reqItems) {
            InvoiceItem it = new InvoiceItem();
            it.setPosition(r.position());
            it.setItemId(r.itemId());
            it.setTitle(r.title());
            it.setQuantity(r.quantity());
            it.setUnit(r.unit() == null || r.unit().isBlank() ? "pcs" : r.unit());
            it.setUnitPriceNet(r.unitPriceNet());
            it.setTaxRate(r.taxRate());
            it.recalcLineTotals();
            out.add(it);
        }
        return out;
    }
}
