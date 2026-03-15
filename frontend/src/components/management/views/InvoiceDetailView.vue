<template>
  <Teleport to="body">
    <div
      v-if="inv"
      class="fixed inset-0 bg-black/35 z-1000 flex items-center justify-center p-4"
      @click.self="emit('close')"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-y-auto flex flex-col"
      >
        <!-- Header -->
        <div
          class="flex items-center justify-between px-6 py-5 border-b border-gray-100 shrink-0 gap-3"
        >
          <div class="flex items-center gap-3 flex-wrap">
            <span class="text-lg font-bold text-gray-900 tracking-tight">{{
              inv.invoiceNumber
            }}</span>
            <span
              class="inline-block px-2.5 py-1 rounded-full text-xs font-semibold"
              :class="statusClass(inv.status)"
            >
              {{ statusLabel(inv.status) }}
            </span>
          </div>
          <div class="flex items-center gap-2 flex-wrap">
            <button
              class="px-4 py-2 text-sm text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
              @click="emit('print', inv)"
            >
              Drucken
            </button>
            <button
              class="px-4 py-2 text-sm text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
              @click="emit('edit', inv)"
            >
              Bearbeiten
            </button>
            <button
              class="text-gray-400 hover:text-gray-900 hover:bg-gray-100 px-2 py-1 rounded-lg transition-colors text-base"
              @click="emit('close')"
            >
              ✕
            </button>
          </div>
        </div>

        <!-- Body -->
        <div class="px-6 py-5 flex-1">
          <div class="grid grid-cols-2 gap-5 mb-5">
            <div>
              <p
                class="text-xs font-bold uppercase tracking-wide text-gray-400 mb-2"
              >
                Kunde
              </p>
              <p class="font-semibold text-gray-900">
                {{ inv.customer?.forename }} {{ inv.customer?.surname }}
              </p>
              <p class="text-gray-400 text-sm">{{ inv.customer?.street }}</p>
              <p class="text-gray-400 text-sm">
                {{ inv.customer?.zip }} {{ inv.customer?.city }}
              </p>
            </div>
            <div>
              <p
                class="text-xs font-bold uppercase tracking-wide text-gray-400 mb-2"
              >
                Rechnungsinfo
              </p>
              <div class="flex justify-between text-sm text-gray-600 py-1">
                <span class="text-gray-400">Nummer</span
                ><span>{{ inv.invoiceNumber }}</span>
              </div>
              <div class="flex justify-between text-sm text-gray-600 py-1">
                <span class="text-gray-400">Datum</span
                ><span>{{ fmtDate(inv.issueDate) }}</span>
              </div>
              <div class="flex justify-between text-sm text-gray-600 py-1">
                <span class="text-gray-400">Leistungsdatum</span
                ><span>{{ fmtDate(inv.serviceDate) }}</span>
              </div>
              <div class="flex justify-between text-sm text-gray-600 py-1">
                <span class="text-gray-400">Währung</span
                ><span>{{ inv.currency }}</span>
              </div>
            </div>
          </div>

          <table class="w-full border-collapse text-sm mt-5">
            <thead>
              <tr class="border-b border-gray-100">
                <th
                  class="px-2 py-2 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
                >
                  Pos.
                </th>
                <th
                  class="px-2 py-2 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
                >
                  Bezeichnung
                </th>
                <th
                  class="px-2 py-2 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
                >
                  Menge
                </th>
                <th
                  class="px-2 py-2 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
                >
                  Einheit
                </th>
                <th
                  class="px-2 py-2 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
                >
                  Netto/Einh.
                </th>
                <th
                  class="px-2 py-2 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
                >
                  MwSt.
                </th>
                <th
                  class="px-2 py-2 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
                >
                  Netto
                </th>
                <th
                  class="px-2 py-2 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
                >
                  Brutto
                </th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="it in inv.items"
                :key="it.id"
                class="border-b border-gray-50 last:border-0"
              >
                <td class="px-2 py-2.5 text-gray-400">{{ it.position }}</td>
                <td class="px-2 py-2.5 text-gray-800">{{ it.title }}</td>
                <td class="px-2 py-2.5 text-right tabular-nums text-gray-700">
                  {{ it.quantity }}
                </td>
                <td class="px-2 py-2.5 tabular-nums text-gray-500">
                  {{ it.unit }}
                </td>
                <td class="px-2 py-2.5 text-right tabular-nums text-gray-700">
                  {{ fmtMoney(it.unitPriceNet) }}
                </td>
                <td class="px-2 py-2.5 text-right text-gray-400">
                  {{ (it.taxRate * 100).toFixed(0) }}%
                </td>
                <td class="px-2 py-2.5 text-right tabular-nums text-gray-700">
                  {{ fmtMoney(it.netTotal) }}
                </td>
                <td
                  class="px-2 py-2.5 text-right tabular-nums font-semibold text-gray-900"
                >
                  {{ fmtMoney(it.grossTotal) }}
                </td>
              </tr>
            </tbody>
          </table>

          <!-- Totals -->
          <div class="flex flex-col items-end mt-3 gap-0.5">
            <div
              class="flex justify-between gap-12 px-2.5 py-1.5 text-sm text-gray-500 w-72"
            >
              <span>Netto</span><span>{{ fmtMoney(inv.netTotal) }}</span>
            </div>
            <div
              class="flex justify-between gap-12 px-2.5 py-1.5 text-sm text-gray-500 w-72"
            >
              <span>MwSt.</span><span>{{ fmtMoney(inv.taxTotal) }}</span>
            </div>
            <div
              class="flex justify-between gap-12 px-2.5 py-1.5 bg-gray-100 rounded-lg font-bold text-sm text-gray-900 w-72 mt-1"
            >
              <span>Gesamt (Brutto)</span
              ><span>{{ fmtMoney(inv.grossTotal) }}</span>
            </div>
          </div>

          <p
            v-if="inv.notes"
            class="mt-5 text-sm text-gray-400 leading-relaxed"
          >
            {{ inv.notes }}
          </p>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { fmtDate, fmtMoney, statusLabel } from "../managementHelpers";
import type { Invoice } from "../blocks/InvoiceTab.vue";

defineProps<{ inv: Invoice | null }>();
const emit = defineEmits<{
  (e: "close"): void;
  (e: "print", inv: Invoice): void;
  (e: "edit", inv: Invoice): void;
}>();

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
