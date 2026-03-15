<template>
  <section class="flex flex-col gap-4">
    <!-- Toolbar -->
    <div class="flex items-center gap-3 flex-wrap">
      <div
        class="flex items-center gap-2 bg-white border border-gray-200 rounded-lg px-3.5 py-2.5 flex-1 min-w-[180px] max-w-sm"
      >
        <svg
          width="15"
          height="15"
          viewBox="0 0 15 15"
          fill="none"
          class="shrink-0"
        >
          <circle cx="6.5" cy="6.5" r="4.5" stroke="#999" stroke-width="1.4" />
          <path
            d="M10 10l3.5 3.5"
            stroke="#999"
            stroke-width="1.4"
            stroke-linecap="round"
          />
        </svg>
        <input
          v-model="search"
          placeholder="Suchen…"
          class="border-none outline-none text-sm text-gray-800 w-full bg-transparent placeholder:text-gray-300"
        />
      </div>
      <div class="flex gap-1.5 flex-wrap">
        <button
          v-for="s in statuses"
          :key="s.val"
          class="px-4 py-2 rounded-full text-sm border transition-all duration-150"
          :class="
            filter === s.val
              ? 'bg-gray-900 border-gray-900 text-white'
              : 'bg-white border-gray-200 text-gray-500 hover:border-gray-400 hover:text-gray-800'
          "
          @click="filter = s.val"
        >
          {{ s.label }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white border border-gray-200 rounded-xl overflow-hidden">
      <div v-if="loading" class="flex justify-center py-14">
        <div
          class="w-6 h-6 border-2 border-gray-100 border-t-blue-600 rounded-full animate-spin"
        ></div>
      </div>
      <div
        v-else-if="filtered.length === 0"
        class="text-center py-16 text-gray-300 text-sm"
      >
        Keine Rechnungen gefunden
      </div>
      <table v-else class="w-full border-collapse text-sm">
        <thead>
          <tr class="border-b border-gray-100">
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Nummer
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Kunde
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Datum
            </th>
            <th
              class="px-4 py-3 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Netto
            </th>
            <th
              class="px-4 py-3 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Brutto
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Status
            </th>
            <th
              class="px-4 py-3 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Aktionen
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="inv in filtered"
            :key="inv.id"
            class="border-b border-gray-50 last:border-0 hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3.5 tabular-nums text-gray-700">
              {{ inv.invoiceNumber }}
            </td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-2.5">
                <div
                  class="w-9 h-9 rounded-full bg-gradient-to-br from-blue-50 to-blue-200 flex items-center justify-center text-xs font-bold text-blue-600 shrink-0"
                >
                  {{ initials(inv.customer?.forename, inv.customer?.surname) }}
                </div>
                <span class="text-gray-800"
                  >{{ inv.customer?.forename }}
                  {{ inv.customer?.surname }}</span
                >
              </div>
            </td>
            <td class="px-4 py-3.5 text-gray-400">
              {{ fmtDate(inv.issueDate) }}
            </td>
            <td class="px-4 py-3.5 text-right tabular-nums text-gray-700">
              {{ fmtMoney(inv.netTotal) }}
            </td>
            <td
              class="px-4 py-3.5 text-right tabular-nums font-semibold text-gray-900"
            >
              {{ fmtMoney(inv.grossTotal) }}
            </td>
            <td class="px-4 py-3.5">
              <span
                class="inline-block px-2.5 py-1 rounded-full text-xs font-semibold whitespace-nowrap"
                :class="statusClass(inv.status)"
              >
                {{ statusLabel(inv.status) }}
              </span>
            </td>
            <td class="px-4 py-3.5">
              <div class="flex gap-1.5 justify-end flex-wrap">
                <button class="act" @click="emit('detail', inv)">
                  <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                    <ellipse
                      cx="8"
                      cy="8"
                      rx="6.5"
                      ry="4.5"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                    <circle
                      cx="8"
                      cy="8"
                      r="2"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                  </svg>
                  Ansehen
                </button>
                <button class="act" @click="emit('edit', inv)">
                  <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                    <path
                      d="M10.5 3l2.5 2.5L5 13.5H2.5V11L10.5 3z"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linejoin="round"
                    />
                  </svg>
                  Bearbeiten
                </button>
                <button class="act" @click="emit('duplicate', inv)">
                  <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                    <rect
                      x="4.5"
                      y="4.5"
                      width="9"
                      height="9"
                      rx="1.5"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                    <path
                      d="M2 11V2h9"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                  </svg>
                  Kopieren
                </button>
                <button class="act" @click="emit('print', inv)">
                  <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                    <rect
                      x="3"
                      y="1.5"
                      width="10"
                      height="5"
                      rx="1"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                    <path
                      d="M3 6.5H1.5v7H3M13 6.5h1.5v7H13"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                    <rect
                      x="3"
                      y="9.5"
                      width="10"
                      height="5.5"
                      rx="1"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                  </svg>
                  Drucken
                </button>
                <!-- Löschen nur bei DRAFT (GoBD) -->
                <button
                  v-if="inv.status === 'DRAFT'"
                  class="act act-del"
                  @click="emit('delete', inv)"
                >
                  <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                    <path
                      d="M2 4.5h12M5.5 4.5V3h5v1.5M6 7.5v5.5M10 7.5v5.5"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                    <path
                      d="M3 4.5l.5 10h9l.5-10"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linejoin="round"
                    />
                  </svg>
                  Löschen
                </button>
                <!-- Stornieren bei SENT / PAID (GoBD) -->
                <button
                  v-if="inv.status === 'SENT' || inv.status === 'PAID'"
                  class="act act-del"
                  @click="emit('storno', inv)"
                >
                  <svg width="14" height="14" viewBox="0 0 16 16" fill="none">
                    <path
                      d="M3 3l10 10M13 3L3 13"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                  </svg>
                  Stornieren
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { fmtDate, fmtMoney, initials, statusLabel } from "../managementHelpers";

interface Customer {
  id: string;
  forename: string;
  surname: string;
  street: string;
  zip: string;
  city: string;
}
interface InvItem {
  id: string;
  position: number;
  itemId: string | null;
  title: string;
  quantity: number;
  unit: string;
  unitPriceNet: number;
  taxRate: number;
  netTotal: number;
  taxTotal: number;
  grossTotal: number;
}
export interface Invoice {
  id: string;
  invoiceNumber: string;
  customer: Customer;
  issueDate: string;
  serviceDate: string;
  currency: string;
  notes: string;
  status: string;
  netTotal: number;
  taxTotal: number;
  grossTotal: number;
  items: InvItem[];
}

const props = defineProps<{ invoices: Invoice[]; loading: boolean }>();
const emit = defineEmits<{
  (e: "detail", inv: Invoice): void;
  (e: "edit", inv: Invoice): void;
  (e: "duplicate", inv: Invoice): void;
  (e: "print", inv: Invoice): void;
  (e: "delete", inv: Invoice): void;
  (e: "storno", inv: Invoice): void;
}>();

const search = ref("");
const filter = ref("Alle");
const statuses = [
  { val: "Alle", label: "Alle" },
  { val: "DRAFT", label: "Entwurf" },
  { val: "SENT", label: "Versendet" },
  { val: "PAID", label: "Bezahlt" },
  { val: "CANCELLED", label: "Storniert" },
];
const filtered = computed(() => {
  let l = props.invoices;
  const q = search.value.toLowerCase();
  if (q)
    l = l.filter(
      (i) =>
        (i.invoiceNumber || "").toLowerCase().includes(q) ||
        `${i.customer?.forename} ${i.customer?.surname}`
          .toLowerCase()
          .includes(q),
    );
  if (filter.value !== "Alle") l = l.filter((i) => i.status === filter.value);
  return l;
});

function statusClass(s?: string) {
  return (
    {
      DRAFT: "bg-gray-100 text-gray-500",
      SENT: "bg-blue-50 text-blue-700",
      PAID: "bg-green-50 text-green-700",
      CANCELLED: "bg-red-50 text-red-600",
    }[s ?? ""] ?? "bg-gray-100 text-gray-500"
  );
}
</script>

<style scoped>
.act {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  color: #6b7280;
  font-size: 12px;
  font-weight: 500;
  font-family: inherit;
  transition: all 0.13s;
  white-space: nowrap;
}
.act:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
  color: #111827;
}
.act-del:hover {
  background: #fef2f2;
  border-color: #fca5a5;
  color: #dc2626;
}
</style>
