<template>
  <aside class="editor-panel">
    <div class="editor-header">
      <h2 class="editor-title">Creator</h2>
    </div>

    <div class="editor-scroll">

      <!-- ── Unternehmen ─────────────────────────────────────────────── -->
      <section class="editor-section">
        <h3 class="editor-section__title">Unternehmen</h3>
        <Company />
      </section>

      <div class="editor-divider" />

      <!-- ── Kundendaten ─────────────────────────────────────────────── -->
      <section class="editor-section">
        <h3 class="editor-section__title">Kundendaten</h3>
        <Customer @open-customer-dialog="emit('open-customer-dialog')" />
      </section>

      <div class="editor-divider" />

      <!-- ── Rechnungsinfo ───────────────────────────────────────────── -->
      <section class="editor-section">
        <h3 class="editor-section__title">Rechnungsinfo</h3>
        <InvoiceMeta />
      </section>

      <div class="editor-divider" />

      <!-- ── Anrede ──────────────────────────────────────────────────── -->
      <section class="editor-section">
        <h3 class="editor-section__title">Anrede</h3>
        <div class="field-group">
          <label>Rechnungsnnummer</label>
          <textarea
            v-model="invoiceData.intro"
            rows="5"
            placeholder="Sehr geehrte/r Frau/Herr Mustermann, vielen dank..."
          ></textarea>
        </div>
      </section>

      <div class="editor-divider" />

      <!-- ── Tabelle ─────────────────────────────────────────────────── -->
      <section class="editor-section">
        <h3 class="editor-section__title">Tabelle</h3>
        <LineItems @open-catalog-dialog="(ri) => emit('open-catalog-dialog', ri)" />
      </section>

      <div class="editor-divider" />

      <!-- ── Bankverbindung ──────────────────────────────────────────── -->
      <section class="editor-section">
        <h3 class="editor-section__title">Bankverbindung</h3>
        <Banking />
      </section>

      <div class="editor-divider" />

      <!-- ── Abschluss ───────────────────────────────────────────────── -->
      <section class="editor-section">
        <h3 class="editor-section__title">Abschluss</h3>
        <Closing />
      </section>

    </div>
  </aside>
</template>

<script setup lang="ts">
import { invoiceData } from "../../composables/invoiceData";

import Company from "./blocks/Company.vue";
import Customer from "./blocks/Customer.vue";
import InvoiceMeta from "./blocks/InvoiceMeta.vue";
import LineItems from "./blocks/LineItems.vue";
import Banking from "./blocks/Banking.vue";
import Closing from "./blocks/Closing.vue";

const emit = defineEmits<{
  (e: "open-customer-dialog"): void;
  (e: "open-catalog-dialog", rowIndex: number): void;
}>();
</script>

<style scoped>
.editor-panel {
  width: 50%;
  min-width: 300px;
  background: #fff;
  border-right: 1px solid #e8e8e4;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-header {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #f0f0ec;
  flex-shrink: 0;
}

.editor-title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: #111;
  margin: 0;
}

.editor-scroll {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 40px;
}

.editor-section {
  padding: 24px 24px 20px;
}

.editor-section__title {
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.3px;
  color: #111;
  margin: 0 0 16px;
}

.editor-divider {
  height: 1px;
  background: #f0f0ec;
  margin: 0 24px;
}

@media (max-width: 1024px) {
  .editor-panel {
    width: 100%;
    overflow: visible;
  }

  .editor-scroll {
    overflow: visible;
  }
}
</style>