package de.mirac.invoice.invoice.backend.domain.items;

import de.mirac.invoice.invoice.backend.domain.items.ItemService;
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

    public record CreateItemRequest(
        String name,
        String description,
        String defaultUnit,
        Double defaultTaxRate,
        Double unitPriceNet,
        Boolean active
    ) {}

    public record ItemResponse(
        String id,
        String name,
        String description,
        String defaultUnit,
        Double defaultTaxRate,
        Double unitPriceNet,
        Boolean active,
        String createdAt,
        String updatedAt
    ) {
        static ItemResponse from(Item i) {
            return new ItemResponse(
                i.getId(),
                i.getName(),
                i.getDescription(),
                i.getDefaultUnit(),
                i.getDefaultTaxRate(),
                i.getUnitPriceNet(),
                i.getActive(),
                i.getCreatedAt(),
                i.getUpdatedAt()
            );
        }
    }
}
