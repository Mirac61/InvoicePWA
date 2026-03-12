<template>
  <div class="creator-layout">
    <EditorPanel
      @open-customer-dialog="showCustomerDialog = true"
      @open-catalog-dialog="
        (ri) => {
          catalogDialogRow = ri;
          showCatalogDialog = true;
        }
      "
    />

    <PreviewPanel :is-saving="isSaving" @save="saveInvoice" />

    <CustomerDialog v-model="showCustomerDialog" />

    <CatalogItemDialog
      v-model="showCatalogDialog"
      :target-row="catalogDialogRow"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import EditorPanel from "./EditorPanel.vue";
import PreviewPanel from "./PreviewPanel.vue";
import CustomerDialog from "./dialogs/CustomerDialog.vue";
import CatalogItemDialog from "./dialogs/CatalogItemDialog.vue";

import { invoiceData } from "../../composables/invoiceData";
import { sections } from "../../composables/sectionOrdering";
import { selectedCustomerObj } from "../../composables/customerSearch";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";

const isSaving = ref(false);
const showCustomerDialog = ref(false);
const showCatalogDialog = ref(false);
const catalogDialogRow = ref(0);

async function saveInvoice() {
  if (!selectedCustomerObj.value) {
    alert(
      "Bitte zuerst einen Kunden aus der Datenbank auswählen (Kundendaten → Suche).",
    );
    return;
  }

  const tableSection = sections.value.find((s) => s.type === "table");
  if (!tableSection) {
    alert("Keine Tabelle vorhanden.");
    return;
  }

  const cols = invoiceData.tableColumns;
  const labels = cols.map((c) => c.label.toLowerCase());

  const titleIdx = labels.findIndex(
    (l) =>
      l.includes("leist") ||
      l.includes("name") ||
      l.includes("titel") ||
      l.includes("pos"),
  );
  const descIdx = labels.findIndex(
    (l, i) => i > 0 && (l.includes("beschr") || l.includes("desc")),
  );
  const qtyIdx = labels.findIndex(
    (l) =>
      l.includes("anz") ||
      l.includes("qty") ||
      l.includes("meng") ||
      l.includes("aufwand") ||
      l.includes("std"),
  );
  const unitIdx = labels.findIndex(
    (l) => l.includes("einh") || l.includes("unit"),
  );
  const priceIdx = labels.findLastIndex(
    (l) =>
      l.includes("sum") ||
      l.includes("preis") ||
      l.includes("price") ||
      l.includes("betrag"),
  );

  const effectiveTitleIdx = titleIdx >= 0 ? titleIdx : 0;
  const effectivePriceIdx = priceIdx >= 0 ? priceIdx : cols.length - 1;
  const effectiveQtyIdx =
    qtyIdx >= 0 ? qtyIdx : cols.length >= 2 ? cols.length - 2 : 0;

  const items = invoiceData.tableRows
    .filter((row) => row.cells.some((c) => c?.trim()))
    .map((row, idx) => {
      const priceRaw = (row.cells[effectivePriceIdx] ?? "")
        .replace(/[^\d,.-]/g, "")
        .replace(",", ".");
      const qtyRaw = (row.cells[effectiveQtyIdx] ?? "")
        .replace(/[^\d,.]/g, "")
        .replace(",", ".");
      const price = parseFloat(priceRaw) || 0;
      const qty = parseFloat(qtyRaw) || 1;
      const isSum =
        effectivePriceIdx === priceIdx &&
        (labels[priceIdx]?.includes("sum") ||
          labels[priceIdx]?.includes("betrag"));
      const unitNet = isSum && qty > 0 ? price / qty : price;
      const title =
        (row.cells[effectiveTitleIdx] ?? "").trim() || `Position ${idx + 1}`;
      const desc = descIdx >= 0 ? (row.cells[descIdx] ?? "").trim() : "";
      const unit =
        unitIdx >= 0 ? (row.cells[unitIdx] ?? "").trim() || "pcs" : "pcs";
      const linkedRow = row as any;

      return {
        position: idx + 1,
        itemId: linkedRow.linkedItemId ?? null,
        title: desc ? `${title} – ${desc}` : title,
        quantity: Math.max(qty, 0.001),
        unit,
        unitPriceNet: Math.round(unitNet * 100) / 100,
        taxRate: invoiceData.taxRate / 100,
      };
    });

  if (items.length === 0) {
    alert("Mindestens eine Position ist erforderlich.");
    return;
  }

  const payload = {
    customerId: selectedCustomerObj.value.id,
    issueDate: invoiceData.invoiceDate || new Date().toISOString().slice(0, 10),
    serviceDate:
      invoiceData.invoiceDate || new Date().toISOString().slice(0, 10),
    currency: "EUR",
    notes: invoiceData.intro || invoiceData.closingText || "",
    status: "DRAFT",
    items,
  };

  isSaving.value = true;
  try {
    const res = await fetch(`${API_BASE}/invoices`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const err = await res
        .json()
        .catch(() => ({ message: "Unbekannter Fehler" }));
      throw new Error(err.message ?? `HTTP ${res.status}`);
    }
    const created = await res.json();
    invoiceData.invoiceNumber = created.invoiceNumber;
    alert(`✅ Rechnung ${created.invoiceNumber} erfolgreich gespeichert!`);
  } catch (e: any) {
    alert(`❌ Fehler beim Speichern: ${e.message}`);
  } finally {
    isSaving.value = false;
  }
}
</script>

<style>
@import "./creator.css";
</style>
