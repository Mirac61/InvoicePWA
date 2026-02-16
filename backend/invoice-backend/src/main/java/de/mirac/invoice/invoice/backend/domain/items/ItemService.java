package de.mirac.invoice.invoice.backend.domain.items;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item create(Item item) {
        validateDuplicateName(item.getName(), null);
        return itemRepository.save(item);
    }

    public List<Item> listAll() {
        return itemRepository.findAll();
    }

    public Item getById(@NonNull String id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Item not found: " + id));
    }

    public Item update(@NonNull String id, Item input) {
        Item existing = getById(id);

        validateDuplicateName(input.getName(), existing.getId());

        existing.setName(input.getName());
        existing.setDescription(input.getDescription());
        existing.setDefaultUnit(input.getDefaultUnit());
        existing.setDefaultTaxRate(input.getDefaultTaxRate());
        existing.setUnitPriceNet(input.getUnitPriceNet());
        existing.setActive(input.getActive());

        return itemRepository.save(existing);
    }

    public void deleteById(@NonNull String id) {
        if (!itemRepository.existsById(id)) {
            throw new NoSuchElementException("Item not found: " + id);
        }
        itemRepository.deleteById(id);
    }

    public List<Item> searchByName(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameContainingIgnoreCase(query);
    }

    private void validateDuplicateName(String name, String currentItemId) {
        if (name == null || name.isBlank()) {
            return;
        }

        itemRepository.findByNameIgnoreCase(name).ifPresent(found -> {
            boolean isSame = currentItemId != null && currentItemId.equals(found.getId());
            if (!isSame) {
                throw new IllegalArgumentException("An item with this name already exists.");
            }
        });
    }
}
