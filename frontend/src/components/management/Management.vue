<template>
  <div
    class="min-h-screen bg-gray-50 font-sans px-9 py-8 pb-20 flex flex-col gap-6"
  >
    <!-- Tab Bar -->
    <div
      class="flex gap-1 bg-white border border-gray-200 rounded-xl p-1.5 w-fit"
    >
      <button
        v-for="tab in tabs"
        :key="tab.id"
        class="flex items-center gap-2 px-6 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 whitespace-nowrap"
        :class="
          activeTab === tab.id
            ? 'bg-gray-900 text-white'
            : 'text-gray-500 hover:bg-gray-100 hover:text-gray-800'
        "
        @click="activeTab = tab.id"
      >
        <span class="flex" v-html="tab.icon"></span>
        {{ tab.label }}
        <span
          class="text-xs font-bold px-1.5 py-0.5 rounded-full min-w-[20px] text-center"
          :class="
            activeTab === tab.id
              ? 'bg-white/20 text-white'
              : 'bg-black/8 text-inherit'
          "
          >{{ tabCount(tab.id) }}</span
        >
      </button>
    </div>

    <!-- Blocks -->
    <InvoiceTab
      v-if="activeTab === 'invoices'"
      :invoices="invoices"
      :loading="invLoading"
      @detail="detailInv = $event"
      @edit="openEditInv($event)"
      @duplicate="duplicateInv($event)"
      @print="printInv($event)"
      @delete="
        askDelete('Rechnung', $event.invoiceNumber, () => deleteInv($event.id))
      "
      @storno="
        askDelete('Storno für', $event.invoiceNumber, () => stornoInv($event))
      "
    />

    <CustomerTab
      v-if="activeTab === 'customers'"
      :customers="customers"
      :loading="custLoading"
      @create="openCustomerView(null)"
      @edit="openCustomerView($event)"
      @delete="
        askDelete('Kunden', `${$event.forename} ${$event.surname}`, () =>
          deleteCust($event.id),
        )
      "
    />

    <ItemTab
      v-if="activeTab === 'items'"
      :items="items"
      :loading="itemsLoading"
      @create="openItemView(null)"
      @edit="openItemView($event)"
      @toggle="toggleItem($event)"
    />

    <!-- Views -->
    <InvoiceDetailView
      :inv="detailInv"
      @close="detailInv = null"
      @print="printInv($event)"
      @edit="
        openEditInv($event);
        detailInv = null;
      "
    />
    <InvoiceEditView
      :open="editInvOpen"
      :invoice="editingInv"
      @close="editInvOpen = false"
      @saved="onInvSaved($event)"
    />
    <CustomerView
      :open="custViewOpen"
      :customer="editingCust"
      @close="custViewOpen = false"
      @saved="onCustSaved($event)"
    />
    <ItemView
      :open="itemViewOpen"
      :item="editingItem"
      @close="itemViewOpen = false"
      @saved="onItemSaved($event)"
    />
    <DeleteConfirmView
      :confirm="delConfirm"
      :loading="deleting"
      @confirm="runDelete"
      @cancel="delConfirm = null"
    />

    <!-- Toast -->
    <Teleport to="body">
      <transition
        enter-active-class="transition ease-out duration-200"
        enter-from-class="opacity-0 translate-y-2"
        enter-to-class="opacity-100 translate-y-0"
        leave-active-class="transition ease-in duration-150"
        leave-from-class="opacity-100 translate-y-0"
        leave-to-class="opacity-0 translate-y-2"
      >
        <div
          v-if="toast"
          class="fixed bottom-7 right-7 px-5 py-3 rounded-xl text-sm font-semibold text-white shadow-lg z-[9999]"
          :class="toast.type === 'ok' ? 'bg-green-600' : 'bg-red-600'"
        >
          {{ toast.message }}
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import InvoiceTab from "./blocks/InvoiceTab.vue";
import CustomerTab from "./blocks/CustomerTab.vue";
import ItemTab from "./blocks/ItemTab.vue";
import InvoiceDetailView from "./views/InvoiceDetailView.vue";
import InvoiceEditView from "./views/InvoiceEditView.vue";
import CustomerView from "./views/CustomerView.vue";
import ItemView from "./views/ItemView.vue";
import DeleteConfirmView from "./views/DeleteConfirmView.vue";
import type { Invoice } from "./blocks/InvoiceTab.vue";
import type { Customer } from "./blocks/CustomerTab.vue";
import type { Item } from "./blocks/ItemTab.vue";

const API = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";

const activeTab = ref<"invoices" | "customers" | "items">("invoices");
const tabs = [
  {
    id: "invoices",
    label: "Rechnungen",
    icon: '<svg width="16" height="16" viewBox="0 0 16 16" fill="none"><rect x="2" y="1" width="12" height="14" rx="1.5" stroke="currentColor" stroke-width="1.4"/><path d="M5 5h6M5 8h6M5 11h4" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/></svg>',
  },
  {
    id: "customers",
    label: "Kunden",
    icon: '<svg width="16" height="16" viewBox="0 0 16 16" fill="none"><circle cx="8" cy="5.5" r="3" stroke="currentColor" stroke-width="1.4"/><path d="M2 14c0-3.3 2.7-6 6-6s6 2.7 6 6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/></svg>',
  },
  {
    id: "items",
    label: "Artikel",
    icon: '<svg width="16" height="16" viewBox="0 0 16 16" fill="none"><rect x="1.5" y="1.5" width="5.5" height="5.5" rx="1" stroke="currentColor" stroke-width="1.4"/><rect x="9" y="1.5" width="5.5" height="5.5" rx="1" stroke="currentColor" stroke-width="1.4"/><rect x="1.5" y="9" width="5.5" height="5.5" rx="1" stroke="currentColor" stroke-width="1.4"/><rect x="9" y="9" width="5.5" height="5.5" rx="1" stroke="currentColor" stroke-width="1.4"/></svg>',
  },
];
function tabCount(id: string) {
  if (id === "invoices") return invoices.value.length;
  if (id === "customers") return customers.value.length;
  return items.value.length;
}

const invoices = ref<Invoice[]>([]);
const customers = ref<Customer[]>([]);
const items = ref<Item[]>([]);
const invLoading = ref(false);
const custLoading = ref(false);
const itemsLoading = ref(false);

async function load() {
  invLoading.value = true;
  custLoading.value = true;
  itemsLoading.value = true;
  const [r1, r2, r3] = await Promise.allSettled([
    fetch(`${API}/invoices`).then((r) => r.json()),
    fetch(`${API}/customers`).then((r) => r.json()),
    fetch(`${API}/items`).then((r) => r.json()),
  ]);
  if (r1.status === "fulfilled") invoices.value = r1.value;
  if (r2.status === "fulfilled") customers.value = r2.value;
  if (r3.status === "fulfilled") items.value = r3.value;
  invLoading.value = false;
  custLoading.value = false;
  itemsLoading.value = false;
}
onMounted(load);

const detailInv = ref<Invoice | null>(null);
const editInvOpen = ref(false);
const editingInv = ref<Invoice | null>(null);

function openEditInv(inv: Invoice) {
  editingInv.value = inv;
  editInvOpen.value = true;
}
function onInvSaved(updated: Invoice) {
  const idx = invoices.value.findIndex((i) => i.id === updated.id);
  if (idx >= 0) invoices.value.splice(idx, 1, updated);
  toast_("Rechnung gespeichert", "ok");
}
async function duplicateInv(inv: Invoice) {
  const payload = {
    customerId: inv.customer.id,
    issueDate: new Date().toISOString().slice(0, 10),
    serviceDate: inv.serviceDate,
    currency: inv.currency || "EUR",
    notes: inv.notes,
    status: "DRAFT",
    items: (inv.items || []).map((it) => ({
      position: it.position,
      itemId: it.itemId || null,
      title: it.title,
      quantity: it.quantity,
      unit: it.unit,
      unitPriceNet: it.unitPriceNet,
      taxRate: it.taxRate,
    })),
  };
  try {
    const res = await fetch(`${API}/invoices`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const created: Invoice = await res.json();
    invoices.value.unshift(created);
    toast_(`${created.invoiceNumber} dupliziert`, "ok");
  } catch (e: any) {
    toast_(`Fehler: ${e.message}`, "err");
  }
}
async function deleteInv(id: string) {
  deleting.value = true;
  try {
    const res = await fetch(`${API}/invoices/${id}`, { method: "DELETE" });
    if (!res.ok) {
      const err = await res
        .json()
        .catch(() => ({ message: `HTTP ${res.status}` }));
      throw new Error(err.message);
    }
    invoices.value = invoices.value.filter((i) => i.id !== id);
    toast_("Rechnung gelöscht", "ok");
  } catch (e: any) {
    toast_(`Fehler: ${e.message}`, "err");
  } finally {
    deleting.value = false;
  }
}
async function stornoInv(inv: Invoice) {
  deleting.value = true;
  try {
    const res = await fetch(`${API}/invoices/${inv.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        customerId: inv.customer.id,
        issueDate: inv.issueDate,
        serviceDate: inv.serviceDate,
        currency: inv.currency,
        notes: inv.notes,
        status: "CANCELLED",
        items: (inv.items || []).map((it) => ({
          position: it.position,
          itemId: it.itemId || null,
          title: it.title,
          quantity: it.quantity,
          unit: it.unit,
          unitPriceNet: it.unitPriceNet,
          taxRate: it.taxRate,
        })),
      }),
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const updated: Invoice = await res.json();
    const idx = invoices.value.findIndex((i) => i.id === updated.id);
    if (idx >= 0) invoices.value.splice(idx, 1, updated);
    toast_(`${inv.invoiceNumber} storniert`, "ok");
  } catch (e: any) {
    toast_(`Fehler: ${e.message}`, "err");
  } finally {
    deleting.value = false;
  }
}
function printInv(inv: Invoice) {
  const rows = (inv.items || [])
    .map(
      (it) =>
        `<tr><td>${it.position}</td><td>${it.title}</td><td style="text-align:right">${it.quantity}</td><td>${it.unit}</td><td style="text-align:right">${fmtMoney(it.unitPriceNet)}</td><td style="text-align:right">${(it.taxRate * 100).toFixed(0)}%</td><td style="text-align:right">${fmtMoney(it.netTotal)}</td><td style="text-align:right">${fmtMoney(it.grossTotal)}</td></tr>`,
    )
    .join("");
  const html = `<!DOCTYPE html><html><head><meta charset="utf-8"><title>${inv.invoiceNumber}</title><style>body{font-family:Helvetica Neue,sans-serif;font-size:11px;padding:40px;color:#222}table{width:100%;border-collapse:collapse;margin-top:16px}th{background:#f0f0ec;padding:7px 8px;text-align:left;font-size:9px;text-transform:uppercase;color:#666}td{padding:7px 8px;border-bottom:1px solid #f4f4f2}.r{text-align:right}@page{size:A4;margin:15mm}</style></head><body><h2 style="margin:0 0 4px">${inv.invoiceNumber}</h2><table><thead><tr><th>Pos.</th><th>Bezeichnung</th><th class="r">Menge</th><th>Einheit</th><th class="r">Netto/Einh.</th><th class="r">MwSt.</th><th class="r">Netto</th><th class="r">Brutto</th></tr></thead><tbody>${rows}</tbody></table></body></html>`;
  const w = window.open("", "_blank", "width=860,height=1100");
  if (!w) return;
  w.document.write(html);
  w.document.close();
  w.focus();
  w.onload = () => {
    w.print();
    w.close();
  };
  setTimeout(() => {
    if (!w.closed) {
      w.print();
      w.close();
    }
  }, 600);
}
function fmtMoney(n?: number) {
  if (n == null) return "—";
  return (
    n.toLocaleString("de-DE", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + " €"
  );
}

const custViewOpen = ref(false);
const editingCust = ref<Customer | null>(null);
function openCustomerView(c: Customer | null) {
  editingCust.value = c;
  custViewOpen.value = true;
}
function onCustSaved(saved: Customer) {
  const idx = customers.value.findIndex((c) => c.id === saved.id);
  if (idx >= 0) customers.value.splice(idx, 1, saved);
  else customers.value.unshift(saved);
  toast_(editingCust.value?.id ? "Kunde aktualisiert" : "Kunde angelegt", "ok");
}
async function deleteCust(id: string) {
  deleting.value = true;
  try {
    const res = await fetch(`${API}/customers/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    customers.value = customers.value.filter((c) => c.id !== id);
    toast_("Kunde gelöscht", "ok");
  } catch (e: any) {
    toast_(`Fehler: ${e.message}`, "err");
  } finally {
    deleting.value = false;
  }
}

const itemViewOpen = ref(false);
const editingItem = ref<Item | null>(null);
function openItemView(item: Item | null) {
  editingItem.value = item;
  itemViewOpen.value = true;
}
function onItemSaved(saved: Item) {
  const idx = items.value.findIndex((x) => x.id === saved.id);
  if (idx >= 0) items.value.splice(idx, 1, saved);
  else items.value.unshift(saved);
  toast_(
    editingItem.value?.id ? "Artikel aktualisiert" : "Artikel angelegt",
    "ok",
  );
}
async function toggleItem(item: Item) {
  try {
    const res = await fetch(`${API}/items/${item.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: item.name,
        description: item.description,
        defaultUnit: item.defaultUnit,
        unitPriceNet: item.unitPriceNet,
        defaultTaxRate: item.defaultTaxRate,
        active: !item.active,
      }),
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const updated: Item = await res.json();
    const i = items.value.findIndex((x) => x.id === item.id);
    if (i >= 0) items.value.splice(i, 1, updated);
    toast_(`Artikel ${updated.active ? "aktiviert" : "deaktiviert"}`, "ok");
  } catch {
    toast_("Fehler", "err");
  }
}

const delConfirm = ref<{ msg: string; go: () => void } | null>(null);
const deleting = ref(false);
function askDelete(type: string, name: string, action: () => void) {
  delConfirm.value = {
    msg: `${type} "${name}" wirklich unwiderruflich löschen?`,
    go: action,
  };
}
function runDelete() {
  delConfirm.value?.go();
  delConfirm.value = null;
}

const toast = ref<{ message: string; type: "ok" | "err" } | null>(null);
function toast_(message: string, type: "ok" | "err") {
  toast.value = { message, type };
  setTimeout(() => (toast.value = null), 3000);
}
</script>
