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
          placeholder="Name, E-Mail, Stadt…"
          class="border-none outline-none text-sm text-gray-800 w-full bg-transparent placeholder:text-gray-300"
        />
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
        Neuer Kunde
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
        Keine Kunden gefunden
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
              Adresse
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Typ
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              E-Mail
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              Telefon
            </th>
            <th
              class="px-4 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-400"
            >
              USt-IdNr.
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
            v-for="c in filtered"
            :key="c.id"
            class="border-b border-gray-50 last:border-0 hover:bg-gray-50 transition-colors"
          >
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-2.5">
                <div
                  class="w-9 h-9 rounded-full bg-linear-to-br from-blue-50 to-blue-200 flex items-center justify-center text-xs font-bold text-blue-600 shrink-0"
                >
                  {{ initials(c.forename, c.surname) }}
                </div>
                <span class="font-semibold text-gray-900"
                  >{{ c.forename }} {{ c.surname }}</span
                >
              </div>
            </td>
            <td class="px-4 py-3.5 text-gray-600">
              {{ c.street }}, {{ c.zip }} {{ c.city }}
            </td>
            <td class="px-4 py-3.5">
              <span
                class="inline-block px-2.5 py-1 rounded-full text-xs font-semibold"
                :class="
                  c.type === 'BUSINESS'
                    ? 'bg-blue-50 text-blue-700'
                    : 'bg-gray-100 text-gray-500'
                "
              >
                {{ c.type === "BUSINESS" ? "Unternehmen" : "Privat" }}
              </span>
            </td>
            <td class="px-4 py-3.5 text-gray-400">{{ c.email || "—" }}</td>
            <td class="px-4 py-3.5 text-gray-400">{{ c.phone || "—" }}</td>
            <td class="px-4 py-3.5 tabular-nums text-gray-500">
              {{ c.vatId || "—" }}
            </td>
            <td class="px-4 py-3.5">
              <div class="flex gap-1.5 justify-end">
                <button class="act" @click="emit('edit', c)">
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
                <button class="act act-del" @click="emit('delete', c)">
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
import { initials } from "../managementHelpers";

export interface Customer {
  id: string;
  type: string;
  forename: string;
  surname: string;
  street: string;
  zip: string;
  city: string;
  country: string;
  email: string;
  phone: string;
  vatId: string;
}

const props = defineProps<{ customers: Customer[]; loading: boolean }>();
const emit = defineEmits<{
  (e: "create"): void;
  (e: "edit", c: Customer): void;
  (e: "delete", c: Customer): void;
}>();

const search = ref("");
const filtered = computed(() => {
  const q = search.value.toLowerCase();
  if (!q) return props.customers;
  return props.customers.filter((c) =>
    `${c.forename} ${c.surname} ${c.email} ${c.city}`.toLowerCase().includes(q),
  );
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
.act-del:hover {
  background: #fef2f2;
  border-color: #fca5a5;
  color: #dc2626;
}
</style>
