<template>
  <div>
    <div class="field-group customer-search-wrap">
      <label>Kundenname</label>
      <input
        v-model="customerSearch"
        type="text"
        placeholder="z.B. Mustermann..."
        autocomplete="off"
        @input="onCustomerSearchInput"
        @blur="onCustomerSearchBlur"
      />

      <div v-if="customerSuggestions.length > 0" class="customer-dropdown">
        <button
          v-for="c in customerSuggestions"
          :key="c.id"
          class="customer-suggestion-item"
          type="button"
          @mousedown.prevent="selectCustomer(c)"
        >
          <span class="suggestion-name">{{ c.forename }} {{ c.surname }}</span>
          <span class="suggestion-meta"
            >{{ c.street }}, {{ c.zip }} {{ c.city }}</span
          >
        </button>
      </div>

      <span v-if="customerSearchLoading" class="search-hint">Suche läuft…</span>
      <span v-else-if="selectedCustomerObj" class="search-hint search-hint--ok"
        >✓ Kunde verknüpft</span
      >

      <div
        v-else-if="
          customerSearch.length >= 2 &&
          !customerSearchLoading &&
          customerSuggestions.length === 0 &&
          !selectedCustomerObj
        "
        class="customer-not-found"
      >
        <span class="not-found-text">Kein Kunde gefunden.</span>
        <button
          class="btn-create-inline"
          type="button"
          @click="emit('open-customer-dialog')"
        >
          Neu erstellen?
        </button>
      </div>
    </div>

    <div class="field-row">
      <div class="field-group">
        <label>Straße und Nummer</label>
        <input
          v-model="invoiceData.customerStreet"
          type="text"
          placeholder="Berliner Straße 41"
        />
      </div>
      <div class="field-group">
        <label>PLZ, Stadt</label>
        <input
          v-model="invoiceData.customerCity"
          type="text"
          placeholder="12450 Musterstadt"
        />
      </div>
    </div>
    <div class="field-row">
      <div class="field-group">
        <label>Kundennummer</label>
        <input
          v-model="invoiceData.customerId"
          type="text"
          placeholder="469332"
          readonly
          style="background: #f9f9f7; color: #aaa"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { invoiceData } from "../../../composables/invoiceData";
import {
  customerSearch,
  customerSuggestions,
  customerSearchLoading,
  selectedCustomerObj,
  onCustomerSearchInput,
  onCustomerSearchBlur,
  selectCustomer,
} from "../../../composables/customerSearch";

const emit = defineEmits<{
  (e: "open-customer-dialog"): void;
}>();
</script>
