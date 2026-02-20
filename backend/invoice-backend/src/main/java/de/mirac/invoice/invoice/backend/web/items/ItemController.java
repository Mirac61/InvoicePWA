package de.mirac.invoice.invoice.backend.web.items;

import de.mirac.invoice.invoice.backend.domain.items.Item;
import de.mirac.invoice.invoice.backend.domain.items.ItemService;
import de.mirac.invoice.invoice.backend.web.items.dto.CreateItemRequest;
import de.mirac.invoice.invoice.backend.web.items.dto.ItemResponse;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> list(@RequestParam(value = "q", required = false) String q) {
        List<Item> items = (q == null || q.isBlank())
            ? itemService.listAll()
            : itemService.searchByName(q);

        return items.stream().map(ItemResponse::from).toList();
    }

    @PostMapping
    public ItemResponse create(@RequestBody CreateItemRequest request) {
        Item item = new Item();
        item.setName(request.name());
        item.setDescription(request.description());
        item.setDefaultUnit(request.defaultUnit());
        item.setDefaultTaxRate(request.defaultTaxRate() == null ? 0.0 : request.defaultTaxRate());
        item.setUnitPriceNet(request.unitPriceNet());
        item.setActive(request.active() == null ? true : request.active());

        return ItemResponse.from(itemService.create(item));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        itemService.deleteById(id);
    }
}
