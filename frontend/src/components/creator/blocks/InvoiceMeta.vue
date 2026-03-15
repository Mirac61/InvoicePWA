<script setup lang="ts">
import { ref } from "vue";
import { invoiceData } from "../../../composables/invoiceData";

const dueDays = ref<14 | 30 | null>(14);
const manualDays = ref<number>(14);

function applyDueDays() {
  const days = dueDays.value === null ? manualDays.value : dueDays.value;
  if (!days) return;
  const base = invoiceData.invoiceDate
    ? new Date(invoiceData.invoiceDate)
    : new Date();
  base.setDate(base.getDate() + days);
  invoiceData.dueDate = base.toISOString().slice(0, 10);
}
</script>

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
        <input
          v-model="invoiceData.invoiceDate"
          type="date"
          @change="if (dueDays !== null) applyDueDays();"
        />
      </div>
    </div>
    <div class="field-row">
      <div class="field-group">
        <label>Fälligkeitsdatum</label>
        <select v-model="dueDays" @change="applyDueDays">
          <option :value="14">14 Tage</option>
          <option :value="30">30 Tage</option>
          <option :value="null">Manuell</option>
        </select>
        <input
          type="number"
          v-if="dueDays === null"
          v-model="manualDays"
          @change="applyDueDays()"
        />
      </div>
    </div>
    <button class="btn-add-entry" type="button">+ Weiterer Eintrag</button>
  </div>
</template>
