<template>
  <div>
    <div class="field-group">
      <label>Schlusstext</label>
      <textarea
        v-model="invoiceData.closingText"
        rows="3"
        placeholder="Wir bedanken uns für Ihren Auftrag."
      ></textarea>
    </div>
    <div class="field-group">
      <label>Fußzeile</label>
      <input
        v-model="invoiceData.footerText"
        type="text"
        placeholder="Logo oHG | Musterstraße 21 | Tel: ..."
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { invoiceData } from "../../../composables/invoiceData";
</script>
