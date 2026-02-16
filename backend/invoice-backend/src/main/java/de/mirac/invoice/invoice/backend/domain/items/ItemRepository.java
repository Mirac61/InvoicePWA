package de.mirac.invoice.invoice.backend.domain.items;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, String> {
    Optional<Item> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Item> findByNameContainingIgnoreCase(String name);
}
