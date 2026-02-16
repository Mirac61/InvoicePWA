<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import {
  createItem,
  deleteItem,
  listItems,
  type ItemResponse,
} from "../services/itemApi";

type DefaultType = "SERVICE" | "PRODUCT";

interface ItemRow {
  id: string;
  name: string;
  price: number;
  defaultUnit: string | null;
}

const items = ref<ItemRow[]>([]);
const isLoading = ref(false);
const errorMessage = ref("");

const searchQuery = ref("");
const selectedItemType = ref<DefaultType>("SERVICE");

const newItemForm = ref<{ name: string; price: number | null }>({
  name: "",
  price: null,
});

// Returns the unit label based on the item type
function getUnitForType(type: DefaultType): string {
  return type === "PRODUCT" ? "pcs" : "hrs";
}

// Checks if the given unit is a product type unit
function isProductType(unit: string | null): boolean {
  const u = (unit ?? "").toLowerCase();
  return u.includes("pcs");
}

// Returns the display label for the item type
function getItemTypeLabel(unit: string | null): string {
  return isProductType(unit) ? "Product" : "Service";
}

// Maps an ItemResponse object to an ItemRow object
function mapToItemRow(item: ItemResponse): ItemRow {
  return {
    id: item.id,
    name: item.name,
    price: item.unitPriceNet,
    defaultUnit: (item as any).defaultUnit ?? null,
  };
}

// Computed property for filtering items based on search query
const filteredItems = computed(() => {
  const query = searchQuery.value.trim().toLowerCase();
  if (!query) return items.value;

  return items.value.filter((item) => {
    const nameMatch = item.name.toLowerCase().includes(query);
    const typeMatch = getItemTypeLabel(item.defaultUnit).toLowerCase().includes(query);
    return nameMatch || typeMatch;
  });
});

// Loads items from the API
async function loadItems(): Promise<void> {
  isLoading.value = true;
  errorMessage.value = "";
  try {
    const fetchedItems = await listItems();
    items.value = fetchedItems.map(mapToItemRow);
  } catch (e) {
    errorMessage.value = e instanceof Error ? e.message : "Error loading items";
  } finally {
    isLoading.value = false;
  }
}

// Adds a new item
async function addItem(): Promise<void> {
  const name = newItemForm.value.name.trim();
  const price = Number(newItemForm.value.price);

  if (!name) {
    alert("Name is required.");
    return;
  }

  if (!Number.isFinite(price) || price <= 0) {
    alert("Price must be greater than 0.");
    return;
  }

  errorMessage.value = "";
  try {
    const createdItem = await createItem({
      name,
      unitPriceNet: price,
      defaultTaxRate: 0,
      active: true,
      defaultUnit: getUnitForType(selectedItemType.value),
      description: null,
    } as any);

    items.value.unshift(mapToItemRow(createdItem));
    newItemForm.value = { name: "", price: null };
  } catch (e) {
    errorMessage.value = e instanceof Error ? e.message : "Error creating item";
  }
}

// Deletes an item by ID
async function deleteItemById(id: string): Promise<void> {
  if (!confirm("Are you sure you want to delete this item?")) return;

  errorMessage.value = "";
  try {
    await deleteItem(id);
    items.value = items.value.filter((item) => item.id !== id);
  } catch (e) {
    errorMessage.value = e instanceof Error ? e.message : "Error deleting item";
  }
}

onMounted(loadItems);
</script>

<template>
  <div class="page-layout">
    <header class="header-layout">
      <div>
        <h1 class="title-text">Items</h1>
        <p class="subtitle-text">
          Articles and services for your invoices
        </p>
      </div>
    </header>

    <section class="panel-layout">
      <div class="toolbar-layout">
        <div class="search-layout">
          <label class="label-text" for="search">Search</label>
          <input
            id="search"
            v-model="searchQuery"
            class="input-text"
            placeholder="Enter name or type"
            autocomplete="off"
          />
        </div>

        <div class="type-selection-box">
          <div class="label-text">Default for new entries</div>

          <div class="type-selection-row">
            <label
              class="control-chip"
              :class="{ 'is-active': selectedItemType === 'SERVICE' }"
            >
              <input type="radio" value="SERVICE" v-model="selectedItemType" />
              Service
            </label>

            <label
              class="control-chip"
              :class="{ 'is-active': selectedItemType === 'PRODUCT' }"
            >
              <input type="radio" value="PRODUCT" v-model="selectedItemType" />
              Product
            </label>
          </div>

          <div class="hint-text">
            New entries will automatically be saved as
            <b>{{
              selectedItemType === "PRODUCT" ? "Product" : "Service"
            }}</b>
          </div>
        </div>
      </div>

      <div class="divider-layout"></div>

      <form class="create-layout" @submit.prevent="addItem">
        <div class="section-title-text">Quick Add</div>

        <div class="create-grid-layout">
          <div class="field-layout">
            <label class="label-text" for="name">Name</label>
            <input
              id="name"
              v-model="newItemForm.name"
              class="input-text"
              placeholder="e.g. shorten trousers"
            />
          </div>

          <div class="field-layout">
            <label class="label-text" for="price">Price net</label>
            <input
              id="price"
              v-model.number="newItemForm.price"
              class="input-text"
              type="number"
              step="0.01"
              min="0"
              placeholder="e.g. 20.00"
            />
          </div>

          <div class="field-layout field-action-layout">
            <button class="button-primary" type="submit">Add</button>
          </div>
        </div>
      </form>

      <p v-if="isLoading" class="hint-text">Loading…</p>
      <p v-else-if="errorMessage" class="error-text">{{ errorMessage }}</p>
    </section>

    <section class="panel-layout">
      <div class="list-header-layout">
        <div class="section-title-text">List</div>
        <div class="hint-text">{{ filteredItems.length }} entries</div>
      </div>

      <div v-if="!isLoading && !errorMessage" class="table-frame">
        <table class="default-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Type</th>
              <th class="text-right">Price net</th>
              <th class="text-right">Action</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="filteredItems.length === 0">
              <td colspan="4" class="table-empty">No items found.</td>
            </tr>

            <tr v-for="item in filteredItems" :key="item.id">
              <td class="text-strong">{{ item.name }}</td>

              <td>
                <span
                  class="type-badge"
                  :class="
                    getItemTypeLabel(item.defaultUnit) === 'Product'
                      ? 'is-product'
                      : 'is-service'
                  "
                >
                  {{ getItemTypeLabel(item.defaultUnit) }}
                </span>
              </td>

              <td class="text-right text-mono">{{ item.price.toFixed(2) }}</td>

              <td class="text-right">
                <button
                  class="delete-button"
                  type="button"
                  @click="deleteItemById(item.id)"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page-layout {
  max-width: 1280px;
  margin: 22px auto;
  padding: 0 18px 40px 18px;
}

.header-layout {
  display: flex;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 16px;
}

.title-text {
  margin: 0;
  font-size: 34px;
  letter-spacing: 0.2px;
}

.subtitle-text {
  margin: 6px 0 0 0;
  opacity: 0.75;
  font-size: 14px;
}

.panel-layout {
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 14px;
}

.toolbar-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 14px;
  align-items: end;
}

.search-layout {
  display: grid;
}

.label-text {
  display: block;
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 6px;
}

.input-text {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid rgba(0, 0, 0, 0.16);
  border-radius: 10px;
  outline: none;
  font-size: 15px;
}

.input-text:focus {
  border-color: rgba(0, 0, 0, 0.3);
}

.type-selection-box {
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.02);
}

.type-selection-row {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}

.control-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.14);
  cursor: pointer;
  user-select: none;
  font-size: 14px;
  background: #fff;
}

.control-chip input {
  accent-color: #111;
}

.control-chip.is-active {
  border-color: rgba(0, 0, 0, 0.34);
  background: rgba(0, 0, 0, 0.04);
}

.hint-text {
  margin-top: 10px;
  font-size: 13px;
  opacity: 0.8;
}

.divider-layout {
  height: 1px;
  background: rgba(0, 0, 0, 0.08);
  margin: 14px 0;
}

.section-title-text {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 12px;
}

.create-grid-layout {
  display: grid;
  grid-template-columns: 1.6fr 240px 220px;
  gap: 12px;
  align-items: end;
}

.field-layout {
  display: grid;
}

.field-action-layout {
  display: flex;
  justify-content: end;
}

.button-primary {
  width: 100%;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.18);
  background: #111;
  color: #fff;
  font-size: 15px;
  cursor: pointer;
  font-weight: 700;
}

.button-primary:hover {
  opacity: 0.92;
}

.button-primary:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.18);
}

.list-header-layout {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;
}

.table-frame {
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  overflow: hidden;
}

.default-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 15px;
}

.default-table thead th {
  text-align: left;
  padding: 12px 14px;
  background: rgba(0, 0, 0, 0.04);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  font-weight: 700;
}

.default-table td {
  padding: 14px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

.default-table tbody tr:nth-child(even) td {
  background: rgba(0, 0, 0, 0.018);
}

.default-table tbody tr:hover td {
  background: rgba(0, 0, 0, 0.035);
}

.default-table tr:last-child td {
  border-bottom: none;
}

.text-right {
  text-align: right;
}

.text-mono {
  font-variant-numeric: tabular-nums;
}

.text-strong {
  font-weight: 600;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 13px;
  border: 1px solid rgba(0, 0, 0, 0.14);
}

.type-badge.is-service {
  background: rgba(0, 0, 0, 0.03);
}

.type-badge.is-product {
  background: rgba(0, 0, 0, 0.06);
}

.delete-button {
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid rgba(185, 28, 28, 0.55);
  background: rgba(185, 28, 28, 0.08);
  color: rgb(185, 28, 28);
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
}

.delete-button:hover {
  background: rgba(185, 28, 28, 0.14);
  border-color: rgba(185, 28, 28, 0.75);
}

.delete-button:active {
  background: rgba(185, 28, 28, 0.2);
}

.delete-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(185, 28, 28, 0.25);
}

.error-text {
  margin: 6px 0 0 0;
  color: #b00020;
}

.table-empty {
  text-align: center;
  opacity: 0.7;
  padding: 18px 0;
}

@media (max-width: 980px) {
  .toolbar-layout {
    grid-template-columns: 1fr;
  }

  .create-grid-layout {
    grid-template-columns: 1fr;
  }

  .field-action-layout {
    justify-content: stretch;
  }
}
</style>
