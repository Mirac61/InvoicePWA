<template>
  <div>
    <!-- Column configurator -->
    <div class="table-section-group">
      <span class="table-section-label">Spalten konfigurieren</span>
      <div class="column-list">
        <div
          v-for="(col, ci) in invoiceData.tableColumns"
          :key="ci"
          class="column-row"
        >
          <input
            v-model="col.label"
            type="text"
            class="column-name-input"
            placeholder="Spaltenname"
          />
          <select v-model="col.align" class="column-align-select">
            <option value="left">L</option>
            <option value="center">M</option>
            <option value="right">R</option>
          </select>
          <button class="btn-icon btn-icon--danger" @click="removeColumn(ci)">
            ✕
          </button>
        </div>
      </div>
      <button class="btn-add-col" type="button" @click="addColumn">
        + Spalte
      </button>
    </div>

    <!-- Row editor -->
    <div class="table-section-group">
      <span class="table-section-label">Zeileneinträge</span>
      <div
        v-for="(row, ri) in invoiceData.tableRows"
        :key="ri"
        class="row-editor"
      >
        <span class="row-num">{{ ri + 1 }}</span>
        <div class="row-cells">
          <template v-for="(col, ci) in invoiceData.tableColumns" :key="ci">
            <div v-if="isTitleColumn(ci)" class="row-cell-search-wrap">
              <input
                v-model="row.cells[ci]"
                type="text"
                :placeholder="col.label"
                class="row-cell-input"
                autocomplete="off"
                @input="onItemSearchInput(ri, row.cells[ci])"
                @blur="onItemSearchBlur(ri)"
              />
              <div v-if="itemSuggestions[ri]?.length > 0" class="item-dropdown">
                <button
                  v-for="item in itemSuggestions[ri]"
                  :key="item.id"
                  class="item-suggestion-item"
                  type="button"
                  @mousedown.prevent="selectItem(ri, item)"
                >
                  <span class="suggestion-name">{{ item.name }}</span>
                  <span class="suggestion-meta">
                    {{ formatCurrency(item.unitPriceNet) }} · MwSt
                    {{ (item.defaultTaxRate * 100).toFixed(0) }}%<span
                      v-if="item.defaultUnit"
                    >
                      · {{ item.defaultUnit }}</span
                    >
                  </span>
                </button>
              </div>
              <div
                v-else-if="
                  (row.cells[ci] ?? '').length >= 2 &&
                  !itemSearchLoading[ri] &&
                  itemSuggestions[ri]?.length === 0 &&
                  !row.linkedItemId
                "
                class="customer-not-found"
              >
                <span class="not-found-text">Nicht gefunden.</span>
                <button
                  class="btn-create-inline"
                  type="button"
                  @mousedown.prevent="emit('open-catalog-dialog', ri)"
                >
                  Neu erstellen?
                </button>
              </div>
              <span
                v-if="row.linkedItemId"
                class="search-hint search-hint--ok"
                style="font-size: 10px"
                >✓ Artikel verknüpft</span
              >
            </div>
            <input
              v-else
              v-model="row.cells[ci]"
              type="text"
              :placeholder="col.label"
              class="row-cell-input"
            />
          </template>
        </div>
        <button class="btn-icon btn-icon--danger" @click="removeRow(ri)">
          ✕
        </button>
      </div>
      <button class="btn-add-row" type="button" @click="addRow">
        + Position hinzufügen
      </button>
    </div>

    <!-- Totals configuration -->
    <div class="table-section-group">
      <span class="table-section-label">Summen</span>
      <div class="field-row">
        <div class="field-group">
          <label>MwSt. (%)</label>
          <input
            v-model.number="invoiceData.taxRate"
            type="number"
            min="0"
            max="100"
            placeholder="19"
          />
        </div>
        <div class="field-group">
          <label>Rabatt (%)</label>
          <input
            v-model.number="invoiceData.discountRate"
            type="number"
            min="0"
            max="100"
            placeholder="0"
          />
        </div>
      </div>
      <div class="toggle-grid">
        <label class="toggle-label">
          <input type="checkbox" v-model="invoiceData.showSubtotal" />
          Zwischensumme anzeigen
        </label>
        <label class="toggle-label">
          <input type="checkbox" v-model="invoiceData.showTax" />
          MwSt. anzeigen
        </label>
        <label class="toggle-label">
          <input type="checkbox" v-model="invoiceData.showDiscount" />
          Rabatte anzeigen
        </label>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  invoiceData,
  formatCurrency,
  addColumn,
  removeColumn,
  addRow,
  removeRow,
} from "../../../composables/invoiceData";
import {
  itemSuggestions,
  itemSearchLoading,
  isTitleColumn,
  onItemSearchInput,
  onItemSearchBlur,
  selectItem,
} from "../../../composables/catalogSearch";

const emit = defineEmits<{
  (e: "open-catalog-dialog", rowIndex: number): void;
}>();
</script>
