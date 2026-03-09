import { ref } from "vue";
import {
  listItems,
  createItem as apiCreateItem,
  type ItemResponse,
  type CreateItemRequest,
} from "../services/ItemAPI";
import { invoiceData, formatCurrency } from "./invoiceData";

export type { ItemResponse };

export const itemSuggestions = ref<Record<number, ItemResponse[]>>({});
export const itemSearchLoading = ref<Record<number, boolean>>({});
const itemSearchTimers = ref<Record<number, ReturnType<typeof setTimeout>>>({});

let allItemsCache: ItemResponse[] | null = null;

async function fetchAllItems(): Promise<ItemResponse[]> {
  if (allItemsCache) return allItemsCache;
  try {
    allItemsCache = await listItems();
  } catch {
    allItemsCache = [];
  }
  return allItemsCache!;
}

export function invalidateItemsCache() {
  allItemsCache = null;
}

async function searchItems(query: string): Promise<ItemResponse[]> {
  const all = await fetchAllItems();
  const q = query.toLowerCase();
  return all
    .filter((i) => i.active && i.name.toLowerCase().includes(q))
    .slice(0, 6);
}

export function isTitleColumn(ci: number): boolean {
  const labels = invoiceData.tableColumns.map((c) => c.label.toLowerCase());
  const titleIdx = labels.findIndex(
    (l) =>
      l.includes("leist") ||
      l.includes("name") ||
      l.includes("titel") ||
      l.includes("artikel"),
  );
  if (titleIdx >= 0) return ci === titleIdx;
  const firstIsPos =
    labels[0]?.includes("pos") ||
    labels[0]?.includes("nr") ||
    labels[0]?.includes("num");
  return firstIsPos ? ci === 1 : ci === 0;
}

export function onItemSearchInput(ri: number, value: string) {
  const row = invoiceData.tableRows[ri] as any;
  if (row.linkedItemId) row.linkedItemId = null;

  if (itemSearchTimers.value[ri]) clearTimeout(itemSearchTimers.value[ri]);
  if (!value || value.length < 2) {
    itemSuggestions.value[ri] = [];
    return;
  }

  itemSearchLoading.value[ri] = true;
  itemSearchTimers.value[ri] = setTimeout(async () => {
    itemSuggestions.value[ri] = await searchItems(value);
    itemSearchLoading.value[ri] = false;
  }, 250);
}

export function onItemSearchBlur(ri: number) {
  setTimeout(() => {
    itemSuggestions.value[ri] = [];
  }, 160);
}

export function selectItem(ri: number, item: ItemResponse) {
  const row = invoiceData.tableRows[ri] as any;
  const cols = invoiceData.tableColumns;
  const labels = cols.map((c) => c.label.toLowerCase());

  const titleColIdx = cols.findIndex((_, i) => isTitleColumn(i));
  const effectiveTitleIdx = titleColIdx >= 0 ? titleColIdx : 0;

  row.cells[effectiveTitleIdx] = item.name;

  const qtyIdx = labels.findIndex(
    (l) =>
      l.includes("anz") ||
      l.includes("qty") ||
      l.includes("meng") ||
      l.includes("aufwand"),
  );
  const unitIdx = labels.findIndex(
    (l) => l.includes("einh") || l.includes("unit"),
  );
  const priceIdx = (() => {
    for (let i = labels.length - 1; i >= 0; i--) {
      const l = labels[i];
      if (
        l.includes("sum") ||
        l.includes("preis") ||
        l.includes("price") ||
        l.includes("betrag")
      )
        return i;
    }
    return -1;
  })();
  const descIdx = labels.findIndex(
    (l: string, i: number) =>
      i !== effectiveTitleIdx && (l.includes("beschr") || l.includes("desc")),
  );

  if (descIdx >= 0) row.cells[descIdx] = item.description ?? "";
  if (qtyIdx >= 0) row.cells[qtyIdx] = "1";
  if (unitIdx >= 0) row.cells[unitIdx] = item.defaultUnit ?? "Stk";
  if (priceIdx >= 0) row.cells[priceIdx] = formatCurrency(item.unitPriceNet);

  invoiceData.taxRate = Math.round(item.defaultTaxRate * 100);
  row.linkedItemId = item.id;
  itemSuggestions.value[ri] = [];
}

export async function createCatalogItem(payload: CreateItemRequest) {
  const created = await apiCreateItem(payload);
  invalidateItemsCache();
  return created;
}
