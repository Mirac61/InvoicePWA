<template>
  <main class="preview-panel">
    <div class="preview-header">
      <h2 class="editor-title">Vorschau</h2>
      <div class="preview-actions">
        <select v-model="selectedTemplate" class="template-select">
          <option value="classic">Klassisch</option>
          <option value="modern">Modern</option>
          <option value="minimal">Minimal</option>
        </select>

          <select v-model="selectedFont" class="template-select">
            <option v-for="font in fontOptions" :key="font.label" :value="font.label">
              {{ font.label }}
    </option> 
  </select>
      </div>
    </div>

    <div class="preview-wrapper">
      <div class="preview-scale-container">
        <div
          id="invoice-preview"
          class="invoice-preview"
          :class="`template--${selectedTemplate}`"
          :style="{'--invoice-font': currentFont.family}"
        >
          <template v-for="section in sections" :key="section.id">
            <!-- HEADER -->
            <div v-if="section.type === 'header'" class="inv-header">
              <div class="inv-header__logo">
                <img
                  v-if="invoiceData.logo"
                  :src="invoiceData.logo"
                  class="inv-logo-img"
                  alt="Logo"
                />
                <div v-else class="inv-logo-placeholder">
                  <span class="inv-logo-dot"></span>
                  <span class="inv-logo-wordmark">{{
                    invoiceData.companyName || "Logo"
                  }}</span>
                </div>
              </div>
              <div class="inv-header__company">
                <strong>{{
                  invoiceData.companyName || "Firmenname GmbH"
                }}</strong>
                <span>{{ invoiceData.companyStreet }}</span>
                <span>{{ invoiceData.companyCity }}</span>
              </div>
            </div>

            <!-- CUSTOMER -->
            <div v-if="section.type === 'customer'" class="inv-customer">
              <p>{{ invoiceData.customerName }}</p>
              <p>{{ invoiceData.customerStreet }}</p>
              <p>{{ invoiceData.customerCity }}</p>
            </div>

            <!-- META -->
            <div v-if="section.type === 'meta'" class="inv-meta">
              <div class="inv-meta__grid">
                <template v-if="invoiceData.invoiceNumber">
                  <span class="inv-meta__key">Rechnungs-Nr.:</span>
                  <span>{{ invoiceData.invoiceNumber }}</span>
                </template>
                <template v-if="invoiceData.customerId">
                  <span class="inv-meta__key">Kunden-Nr.:</span>
                  <span>{{ invoiceData.customerId }}</span>
                </template>
                <template v-if="invoiceData.invoiceDate">
                  <span class="inv-meta__key">Rechnungsdatum:</span>
                  <span>{{ formatDate(invoiceData.invoiceDate) }}</span>
                </template>
                <template v-if="invoiceData.dueDate">
                  <span class="inv-meta__key">Fälligkeitsdatum:</span>
                  <span>{{ formatDate(invoiceData.dueDate) }}</span>
                </template>
              </div>
            </div>

            <!-- GREETING -->
            <div
              v-if="section.type === 'greeting' && invoiceData.intro"
              class="inv-greeting"
            >
              <p class="inv-greeting__title">Ihre Rechnung</p>
              <p class="inv-greeting__body">{{ invoiceData.intro }}</p>
            </div>

            <!-- TABLE -->
            <div v-if="section.type === 'table'" class="inv-table-section">
              <table class="inv-table">
                <thead>
                  <tr>
                    <th
                      v-for="(col, ci) in invoiceData.tableColumns"
                      :key="ci"
                      :style="{ textAlign: col.align }"
                    >
                      {{ col.label }}
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(row, ri) in invoiceData.tableRows"
                    :key="ri"
                    :class="{ 'inv-tr--alt': ri % 2 === 1 }"
                  >
                    <td
                      v-for="(col, ci) in invoiceData.tableColumns"
                      :key="ci"
                      :style="{ textAlign: col.align }"
                    >
                      {{ row.cells[ci] || "" }}
                    </td>
                  </tr>
                </tbody>
              </table>
              <div class="inv-totals">
                <div v-if="invoiceData.showSubtotal" class="inv-totals__row">
                  <span>Zwischensumme</span>
                  <span>{{ formatCurrency(subtotal) }}</span>
                </div>
                <div
                  v-if="
                    invoiceData.showDiscount && invoiceData.discountRate > 0
                  "
                  class="inv-totals__row inv-totals__row--discount"
                >
                  <span>Rabatt ({{ invoiceData.discountRate }}%)</span>
                  <span>-{{ formatCurrency(discountAmount) }}</span>
                </div>
                <div
                  v-if="invoiceData.showTax && invoiceData.taxRate > 0"
                  class="inv-totals__row inv-totals__row--tax"
                >
                  <span>MwSt. ({{ invoiceData.taxRate }}%)</span>
                  <span>{{ formatCurrency(taxAmount) }}</span>
                </div>
                <div class="inv-totals__row inv-totals__row--grand">
                  <span>Gesamtsumme</span>
                  <span>{{ formatCurrency(grandTotal) }}</span>
                </div>
              </div>
            </div>

            <!-- BANKING -->
            <div v-if="section.type === 'banking'" class="inv-banking">
              <div class="inv-banking__col">
                <p>{{ invoiceData.companyName }}</p>
                <p>{{ invoiceData.companyStreet }}</p>
                <p>{{ invoiceData.companyCity }}</p>
                <p v-if="invoiceData.taxId">
                  USt-IdNr.: {{ invoiceData.taxId }}
                </p>
                <p v-if="invoiceData.contact">
                  Ansprechpartner: {{ invoiceData.contact }}
                </p>
              </div>
              <div class="inv-banking__col inv-banking__col--right">
                <p>{{ invoiceData.bankName }}</p>
                <p v-if="invoiceData.iban">IBAN: {{ invoiceData.iban }}</p>
                <p v-if="invoiceData.bic">BIC: {{ invoiceData.bic }}</p>
              </div>
            </div>

            <!-- FOOTER -->
            <div v-if="section.type === 'footer'" class="inv-footer">
              <p v-if="invoiceData.paymentNote" class="inv-footer__payment">
                {{ invoiceData.paymentNote }}
              </p>
              <p v-if="invoiceData.closingText" class="inv-footer__closing">
                {{ invoiceData.closingText }}
              </p>
              <div v-if="invoiceData.footerText" class="inv-footer__bar">
                {{ invoiceData.footerText }}
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- Sticky download bar -->
    <div class="download-bar">
      <button
        class="btn-save"
        type="button"
        @click="emit('save')"
        :disabled="isSaving"
      >
        <span v-if="isSaving">⏳ Speichern…</span>
        <span v-else>💾 In DB speichern</span>
      </button>
      <button class="btn-download" type="button" @click="downloadPDF()">
        <Download :size="16" />
        Als PDF herunterladen
      </button>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { Download } from "lucide-vue-next";
import {
  invoiceData,
  formatDate,
  formatCurrency,
  subtotal,
  discountAmount,
  taxAmount,
  grandTotal,
} from "../../composables/invoiceData";
import { sections } from "../../composables/sectionOrdering";
import { downloadPDF } from "../../composables/pdfExport";
import { fontOptions, selectedFont, activeFontOption } from "../../composables/fontSelection";

defineProps<{ isSaving: boolean }>();

const emit = defineEmits<{
  (e: "save"): void;
}>();

const selectedTemplate = ref<"classic" | "modern" | "minimal">("classic");

const currentFont = computed(() => activeFontOption())
</script>
