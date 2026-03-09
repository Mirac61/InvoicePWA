package de.mirac.invoice.invoice.backend.domain.customer;

import java.util.Optional;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer create(Customer customer) {
        validateDuplicateEmail(customer.getEmail(), null);
        return customerRepository.save(customer);
    }

    public List<Customer> listAll() {
        return customerRepository.findAll();
    }

    public Customer getById(@NonNull String id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Customer not found: " + id));
    }

    public Optional<Customer> findById(@NonNull String id) {
        return customerRepository.findById(id);
    }

    public Customer update(@NonNull String id, Customer input) {
        Customer existing = getById(id);

        validateDuplicateEmail(input.getEmail(), existing.getId());

        existing.setType(input.getType());
        existing.setForename(input.getForename());
        existing.setSurname(input.getSurname());
        existing.setStreet(input.getStreet());
        existing.setZip(input.getZip());
        existing.setCity(input.getCity());
        existing.setCountry(input.getCountry());
        existing.setEmail(input.getEmail());
        existing.setPhone(input.getPhone());
        existing.setVatId(input.getVatId());

        return customerRepository.save(existing);
    }

    public void deleteById(@NonNull String id) {
        if (!customerRepository.existsById(id)) {
            throw new NoSuchElementException("Customer not found: " + id);
        }
        customerRepository.deleteById(id);
    }

    public List<Customer> searchByName(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return customerRepository.findByForenameContainingIgnoreCaseOrSurnameContainingIgnoreCase(query, query);
    }

    private void validateDuplicateEmail(String email, String currentCustomerId) {
        if (email == null || email.isBlank()) {
            return;
        }

        customerRepository.findByEmailIgnoreCase(email).ifPresent(found -> {
            boolean isSameCustomer = currentCustomerId != null && currentCustomerId.equals(found.getId());
            if (!isSameCustomer) {
                throw new IllegalArgumentException("A customer with this email already exists.");
            }
        });
    }
}
