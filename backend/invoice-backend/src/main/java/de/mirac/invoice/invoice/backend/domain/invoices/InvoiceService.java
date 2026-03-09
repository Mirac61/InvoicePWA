package de.mirac.invoice.invoice.backend.domain.invoices;

import de.mirac.invoice.invoice.backend.domain.customer.Customer;
import de.mirac.invoice.invoice.backend.domain.customer.CustomerService;
import de.mirac.invoice.invoice.backend.domain.items.ItemRepository;
import de.mirac.invoice.invoice.backend.domain.settings.Settings;
import de.mirac.invoice.invoice.backend.domain.settings.SettingsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final ItemRepository itemRepository;
    private final SettingsService settingsService;

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    public Optional<Invoice> findById(String id) {
        return invoiceRepository.findById(id);
    }

    public Invoice save(Invoice invoice) {
        Customer customer = customerService.findById(invoice.getCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer with ID " + invoice.getCustomer().getId() + " not found."
                ));

        invoice.setCustomer(customer);

        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isBlank()) {
            invoice.setInvoiceNumber(generateInvoiceNumberAndBumpCounter());
        }

        validateItems(invoice.getItems());
        invoice.recalcTotals();

        return invoiceRepository.save(invoice);
    }

    public Invoice update(String id, Invoice updatedInvoice) {
        Invoice existing = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice with ID " + id + " not found."));

        Customer customer = customerService.findById(updatedInvoice.getCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer with ID " + updatedInvoice.getCustomer().getId() + " not found."
                ));

        existing.setCustomer(customer);
        existing.setIssueDate(updatedInvoice.getIssueDate());
        existing.setServiceDate(updatedInvoice.getServiceDate());
        existing.setCurrency(updatedInvoice.getCurrency());
        existing.setNotes(updatedInvoice.getNotes());
        existing.setStatus(updatedInvoice.getStatus());

        existing.setItems(updatedInvoice.getItems());
        validateItems(existing.getItems());
        existing.recalcTotals();

        return invoiceRepository.save(existing);
    }

    public void deleteById(String id) {
        if (!invoiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Invoice with ID " + id + " not found.");
        }
        invoiceRepository.deleteById(id);
    }

    private void validateItems(List<InvoiceItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Invoice items must not be empty.");
        }

        for (InvoiceItem it : items) {
            if (it.getPosition() == null) {
                throw new IllegalArgumentException("Item position is required.");
            }
            if (it.getTitle() == null || it.getTitle().isBlank()) {
                throw new IllegalArgumentException("Item title is required.");
            }
            if (it.getQuantity() == null || it.getQuantity() <= 0) {
                throw new IllegalArgumentException("Item quantity must be > 0.");
            }
            if (it.getUnitPriceNet() == null || it.getUnitPriceNet() < 0) {
                throw new IllegalArgumentException("Item unit price net must be >= 0.");
            }
            if (it.getTaxRate() == null || it.getTaxRate() < 0) {
                throw new IllegalArgumentException("Item tax rate must be >= 0.");
            }

            if (it.getItemId() != null && !it.getItemId().isBlank()) {
                if (!itemRepository.existsById(it.getItemId())) {
                    throw new IllegalArgumentException("Item with ID " + it.getItemId() + " not found.");
                }
            }
        }
    }

    private String generateInvoiceNumberAndBumpCounter() {
        Settings current = settingsService.get();

        String prefix = current.getInvoicePrefix();
        if (prefix == null || prefix.isBlank()) {
            prefix = "INV";
        }

        Integer next = current.getNextInvoiceNo();
        if (next == null || next < 1) {
            next = 1;
        }

        String number = prefix + "-" + String.format("%06d", next);

        current.setNextInvoiceNo(next + 1);
        settingsService.update(current);

        return number;
    }
}
