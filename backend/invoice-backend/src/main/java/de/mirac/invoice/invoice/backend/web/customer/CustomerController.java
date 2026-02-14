package de.mirac.invoice.invoice.backend.web.customer;

import de.mirac.invoice.invoice.backend.domain.customer.Customer;
import de.mirac.invoice.invoice.backend.domain.customer.CustomerService;
import de.mirac.invoice.invoice.backend.web.customer.dto.CreateCustomerRequest;
import de.mirac.invoice.invoice.backend.web.customer.dto.CustomerResponse;
import de.mirac.invoice.invoice.backend.web.customer.dto.UpdateCustomerRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        Customer created = customerService.create(toCustomer(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public List<CustomerResponse> listAll() {
        return customerService.listAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(toResponse(customerService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable String id, @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(toResponse(customerService.update(id, toCustomer(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Customer toCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setType(request.type());
        customer.setName(request.name());
        customer.setStreet(request.street());
        customer.setZip(request.zip());
        customer.setCity(request.city());
        customer.setCountry(request.country());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setVatId(request.vatId());
        return customer;
    }

    private Customer toCustomer(UpdateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setType(request.type());
        customer.setName(request.name());
        customer.setStreet(request.street());
        customer.setZip(request.zip());
        customer.setCity(request.city());
        customer.setCountry(request.country());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setVatId(request.vatId());
        return customer;
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getType(),
            customer.getName(),
            customer.getStreet(),
            customer.getZip(),
            customer.getCity(),
            customer.getCountry(),
            customer.getEmail(),
            customer.getPhone(),
            customer.getVatId(),
            customer.getCreatedAt(),
            customer.getUpdatedAt()
        );
    }
}
