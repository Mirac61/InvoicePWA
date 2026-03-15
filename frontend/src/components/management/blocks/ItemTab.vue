<template>
  <section class="flex flex-col gap-4">
    <div class="flex items-center gap-3 flex-wrap">
      <div
        class="flex items-center gap-2 bg-white border border-gray-200 rounded-lg px-3.5 py-2.5 flex-1 min-w-45 max-w-sm"
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
          placeholder="Artikel suchen…"
          class="border-none outline-none text-sm text-gray-800 w-full bg-transparent placeholder:text-gray-300"
        />
      </div>
      <div class="flex gap-1.5">
        <button
          class="px-4 py-2 rounded-full text-sm border transition-all duration-150"
          :class="
            filter === 'all'
              ? 'bg-gray-900 border-gray-900 text-white'
              : 'bg-white border-gray-200 text-gray-500 hover:border-gray-400'
          "
          @click="filter = 'all'"
        >
          Alle
        </button>
        <button
          class="px-4 py-2 rounded-full text-sm border transition-all duration-150"
          :class="
            filter === 'active'
              ? 'bg-gray-900 border-gray-900 text-white'
              : 'bg-white border-gray-200 text-gray-500 hover:border-gray-400'
          "
          @click="filter = 'active'"
        >
          Aktiv
        </button>
        <button
          class="px-4 py-2 rounded-full text-sm border transition-all duration-150"
          :class="
            filter === 'inactive'
              ? 'bg-gray-900 border-gray-900 text-white'
              : 'bg-white border-gray-200 text-gray-500 hover:border-gray-400'
          "
          @click="filter = 'inactive'"
        >
          Inaktiv
        </button>
      </div>
      <button
        class="flex items-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-semibold transition-colors whitespace-nowrap"
        @click="emit('create')"
      >
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
          <path
            d="M7 1v12M1 7h12"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          />
        </svg>
        Neuer Artikel
      </button>
    </div>

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
        Keine Artikel gefunden
      </div>
      <table v-else class="w-full border-collapse text-sm">
        <thead>
          <tr class="border-b border-gray-100">
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Name
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Beschreibung
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Einheit
            </th>
            <th
              class="px-4 py-3 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Netto-Preis
            </th>
            <th
              class="px-4 py-3 text-right text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              MwSt.
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
            v-for="item in filtered"
            :key="item.id"
            class="border-b border-gray-50 last:border-0 hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3.5 font-semibold text-gray-900">
              {{ item.name }}
            </td>
            <td class="px-4 py-3.5 text-gray-400">
              {{ item.description || "—" }}
            </td>
            <td class="px-4 py-3.5 tabular-nums text-gray-500">
              {{ item.defaultUnit || "pcs" }}
            </td>
            <td class="px-4 py-3.5 text-right tabular-nums text-gray-700">
              {{ fmtMoney(item.unitPriceNet) }}
            </td>
            <td class="px-4 py-3.5 text-right tabular-nums text-gray-700">
              {{ (item.defaultTaxRate * 100).toFixed(0) }}%
            </td>
            <td class="px-4 py-3.5">
              <span
                class="inline-block px-2.5 py-1 rounded-full text-xs font-semibold"
                :class="
                  item.active
                    ? 'bg-green-50 text-green-700'
                    : 'bg-red-50 text-red-600'
                "
              >
                {{ item.active ? "Aktiv" : "Inaktiv" }}
              </span>
            </td>
            <td class="px-4 py-3.5">
              <div class="flex gap-1.5 justify-end">
                <button class="act" @click="emit('edit', item)">
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
                <button class="act" @click="emit('toggle', item)">
                  <svg
                    v-if="item.active"
                    width="14"
                    height="14"
                    viewBox="0 0 16 16"
                    fill="none"
                  >
                    <circle
                      cx="8"
                      cy="8"
                      r="6.5"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                    <path
                      d="M5.5 8l2 2.5 4-4"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                    />
                  </svg>
                  <svg
                    v-else
                    width="14"
                    height="14"
                    viewBox="0 0 16 16"
                    fill="none"
                  >
                    <circle
                      cx="8"
                      cy="8"
                      r="6.5"
                      stroke="currentColor"
                      stroke-width="1.5"
                    />
                    <path
                      d="M5.5 5.5l5 5M10.5 5.5l-5 5"
                      stroke="currentColor"
                      stroke-width="1.5"
                      stroke-linecap="round"
                    />
                  </svg>
                  {{ item.active ? "Deaktivieren" : "Aktivieren" }}
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
import { fmtMoney } from "../managementHelpers";

export interface Item {
  id: string;
  name: string;
  description: string | null;
  defaultUnit: string | null;
  defaultTaxRate: number;
  unitPriceNet: number;
  active: boolean;
}

const props = defineProps<{ items: Item[]; loading: boolean }>();
const emit = defineEmits<{
  (e: "create"): void;
  (e: "edit", item: Item): void;
  (e: "toggle", item: Item): void;
}>();

const search = ref("");
const filter = ref<"all" | "active" | "inactive">("all");
const filtered = computed(() => {
  let l = props.items;
  const q = search.value.toLowerCase();
  if (q)
    l = l.filter(
      (i) =>
        (i.name || "").toLowerCase().includes(q) ||
        (i.description || "").toLowerCase().includes(q),
    );
  if (filter.value === "active") l = l.filter((i) => i.active);
  if (filter.value === "inactive") l = l.filter((i) => !i.active);
  return l;
});
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
</style>
