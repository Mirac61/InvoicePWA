<template>
  <div>
    <div class="field-row">
      <div class="field-group">
        <label>Rechnungsnummer</label>
        <input
          v-model="invoiceData.invoiceNumber"
          type="text"
          placeholder="10230445"
        />
      </div>
      <div class="field-group">
        <label>Rechnungsdatum</label>
        <input v-model="invoiceData.invoiceDate" type="date" />
      </div>
    </div>
    <div class="field-row">
      <div class="field-group">
        <label>Fälligkeitsdatum</label>
        <input v-model="invoiceData.dueDate" type="date" />
      </div>
    </div>
    <button class="btn-add-entry" type="button">+ Weiterer Eintrag</button>
  </div>
</template>

<script setup lang="ts">
import { invoiceData } from "../../../composables/invoiceData";
</script>
