<template>
  <div class="settings-page">
    <header class="header">
      <h1>Settings</h1>
      <p class="subtitle">
        Start with defaults. Switch to company settings when you need it.
      </p>
    </header>

    <section class="panel">
      <div class="mode">
        <button
          class="mode-card"
          :class="{ active: mode === 'defaults' }"
          type="button"
          @click="switchMode('defaults')"
        >
          <div class="mode-title">Standard settings</div>
          <div class="mode-sub">Quick defaults for invoices.</div>
        </button>

        <button
          class="mode-card"
          :class="{ active: mode === 'company' }"
          type="button"
          @click="switchMode('company')"
        >
          <div class="mode-title">Company settings</div>
          <div class="mode-sub">Your business data and invoice defaults.</div>
        </button>
      </div>

      <div class="divider"></div>

      <section v-if="mode === 'defaults'" class="content">
        <div class="head">
          <h2>Standard settings</h2>
          <p class="hint">
            These values are used until you switch to company settings.
          </p>
        </div>

        <form class="form" @submit.prevent>
          <div class="grid grid-2">
            <div class="field">
              <label for="d_invoicePrefix">Invoice prefix</label>
              <input
                id="d_invoicePrefix"
                v-model.trim="defaults.invoicePrefix"
                type="text"
                disabled
              />
            </div>

            <div class="field">
              <label for="d_nextInvoiceNo">Next invoice number</label>
              <input
                id="d_nextInvoiceNo"
                v-model.number="defaults.nextInvoiceNo"
                type="number"
                disabled
              />
            </div>

            <div class="field">
              <label for="d_paymentDays">Payment days</label>
              <input
                id="d_paymentDays"
                v-model.number="defaults.paymentDays"
                type="number"
                disabled
              />
            </div>

            <div class="field">
              <label for="d_defaultVatRate">Default VAT rate</label>
              <input
                id="d_defaultVatRate"
                v-model.number="defaults.defaultVatRate"
                type="number"
                step="0.01"
                disabled
              />
              <small class="small">0.19 means 19%.</small>
            </div>
          </div>

          <div class="field">
            <label for="d_invoiceFooter">Invoice footer</label>
            <textarea
              id="d_invoiceFooter"
              v-model.trim="defaults.invoiceFooter"
              rows="3"
              disabled
            />
          </div>
        </form>
      </section>

      <section v-else class="content">
        <div class="head head-split">
          <div>
            <h2>Company settings</h2>
            <p class="hint">
              Fill in your business details for legal invoices.
            </p>
          </div>

          <div class="actions">
            <button
              v-if="!isEditing"
              class="btn primary"
              type="button"
              @click="isEditing = true"
            >
              Edit
            </button>

            <template v-else>
              <button
                class="btn"
                type="button"
                @click="cancel"
                :disabled="isSaving"
              >
                Cancel
              </button>
              <button
                class="btn primary"
                type="button"
                @click="save"
                :disabled="isSaving"
              >
                Save
              </button>
            </template>
          </div>
        </div>

        <form class="form" @submit.prevent>
          <div class="grid">
            <div class="field">
              <label for="companyName">Company name</label>
              <input
                id="companyName"
                v-model.trim="company.companyName"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="contactName">Contact name</label>
              <input
                id="contactName"
                v-model.trim="company.contactName"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="email">Email</label>
              <input
                id="email"
                v-model.trim="company.email"
                type="email"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="phone">Phone</label>
              <input
                id="phone"
                v-model.trim="company.phone"
                type="tel"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="street">Street</label>
              <input
                id="street"
                v-model.trim="company.street"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="houseNumber">House number</label>
              <input
                id="houseNumber"
                v-model.trim="company.houseNumber"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="zip">ZIP</label>
              <input
                id="zip"
                v-model.trim="company.zip"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="city">City</label>
              <input
                id="city"
                v-model.trim="company.city"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="country">Country</label>
              <input
                id="country"
                v-model.trim="company.country"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="iban">IBAN</label>
              <input
                id="iban"
                v-model.trim="company.iban"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="bic">BIC</label>
              <input
                id="bic"
                v-model.trim="company.bic"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="bankName">Bank name</label>
              <input
                id="bankName"
                v-model.trim="company.bankName"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="accountHolder">Account holder</label>
              <input
                id="accountHolder"
                v-model.trim="company.accountHolder"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="taxNumber">Tax number</label>
              <input
                id="taxNumber"
                v-model.trim="company.taxNumber"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="vatId">VAT ID</label>
              <input
                id="vatId"
                v-model.trim="company.vatId"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="website">Website</label>
              <input
                id="website"
                v-model.trim="company.website"
                type="url"
                :disabled="!isEditing"
              />
            </div>
          </div>

          <div class="grid grid-2">
            <div class="field">
              <label for="invoicePrefix">Invoice prefix</label>
              <input
                id="invoicePrefix"
                v-model.trim="company.invoicePrefix"
                type="text"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="nextInvoiceNo">Next invoice number</label>
              <input
                id="nextInvoiceNo"
                v-model.number="company.nextInvoiceNo"
                type="number"
                min="1"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="paymentDays">Payment days</label>
              <input
                id="paymentDays"
                v-model.number="company.paymentDays"
                type="number"
                min="1"
                :disabled="!isEditing"
              />
            </div>

            <div class="field">
              <label for="defaultVatRate">Default VAT rate</label>
              <input
                id="defaultVatRate"
                v-model.number="company.defaultVatRate"
                type="number"
                min="0"
                max="1"
                step="0.01"
                :disabled="!isEditing"
              />
              <small class="small">0.19 means 19%.</small>
            </div>
          </div>

          <div class="field">
            <label for="invoiceFooter">Invoice footer</label>
            <textarea
              id="invoiceFooter"
              v-model.trim="company.invoiceFooter"
              rows="4"
              :disabled="!isEditing"
            />
          </div>

          <p v-if="message" class="message">{{ message }}</p>
        </form>
      </section>

      <div class="divider"></div>

      <section class="content">
        <div class="head head-split">
          <div>
            <h2>Theme</h2>
            <p class="hint">Changes this device only.</p>
          </div>
        </div>

        <div class="theme">
          <button
            class="theme-btn"
            :class="{ active: theme === 'system' }"
            type="button"
            @click="setTheme('system')"
          >
            System
          </button>
          <button
            class="theme-btn"
            :class="{ active: theme === 'light' }"
            type="button"
            @click="setTheme('light')"
          >
            Light
          </button>
          <button
            class="theme-btn"
            :class="{ active: theme === 'dark' }"
            type="button"
            @click="setTheme('dark')"
          >
            Dark
          </button>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from "vue";

type Defaults = {
  invoicePrefix: string;
  nextInvoiceNo: number;
  paymentDays: number;
  defaultVatRate: number;
  invoiceFooter: string;
};

type CompanySettings = {
  companyName: string;
  contactName?: string;
  street: string;
  houseNumber: string;
  zip: string;
  city: string;
  country: string;
  email: string;
  phone: string;
  website?: string;
  taxNumber?: string;
  vatId?: string;
  iban?: string;
  bic?: string;
  bankName?: string;
  accountHolder?: string;
  invoicePrefix: string;
  nextInvoiceNo: number;
  paymentDays: number;
  defaultVatRate: number;
  invoiceFooter?: string;
};

const mode = ref<"defaults" | "company">("defaults");
const isEditing = ref(false);
const isSaving = ref(false);
const message = ref("");

const theme = ref<"system" | "light" | "dark">("system");

const defaults = reactive<Defaults>({
  invoicePrefix: "INV",
  nextInvoiceNo: 1,
  paymentDays: 14,
  defaultVatRate: 0.19,
  invoiceFooter: "",
});

const emptyCompany = (): CompanySettings => ({
  companyName: "",
  contactName: "",
  street: "",
  houseNumber: "",
  zip: "",
  city: "",
  country: "",
  email: "",
  phone: "",
  website: "",
  taxNumber: "",
  vatId: "",
  iban: "",
  bic: "",
  bankName: "",
  accountHolder: "",
  invoicePrefix: "INV",
  nextInvoiceNo: 1,
  paymentDays: 14,
  defaultVatRate: 0.19,
  invoiceFooter: "",
});

const company = reactive<CompanySettings>(emptyCompany());
const snapshot = ref<CompanySettings>(emptyCompany());

function deepCopy<T>(v: T): T {
  return JSON.parse(JSON.stringify(v)) as T;
}

function loadCompany() {
  const raw = localStorage.getItem("app.companySettings");
  if (!raw) {
    snapshot.value = deepCopy(company);
    return;
  }
  try {
    const parsed = JSON.parse(raw);
    Object.assign(company, { ...emptyCompany(), ...parsed });
    snapshot.value = deepCopy(company);
  } catch {
    snapshot.value = deepCopy(company);
  }
}

function loadTheme() {
  const stored = localStorage.getItem("app.theme");
  if (stored === "light" || stored === "dark" || stored === "system") {
    theme.value = stored;
  }
}

function applyTheme(t: "system" | "light" | "dark") {
  document.documentElement.dataset.theme = t;
}

function setTheme(t: "system" | "light" | "dark") {
  theme.value = t;
  localStorage.setItem("app.theme", t);
  applyTheme(t);
}

function switchMode(target: "defaults" | "company") {
  if (mode.value === target) return;
  if (mode.value === "company" && isEditing.value) cancel();
  mode.value = target;
}

function cancel() {
  Object.assign(company, deepCopy(snapshot.value));
  isEditing.value = false;
  message.value = "Cancelled.";
}

function save() {
  isSaving.value = true;
  message.value = "";
  try {
    snapshot.value = deepCopy(company);
    localStorage.setItem("app.companySettings", JSON.stringify(snapshot.value));
    isEditing.value = false;
    message.value = "Saved.";
  } finally {
    isSaving.value = false;
  }
}

onMounted(() => {
  loadCompany();
  loadTheme();
  applyTheme(theme.value);
});

watch(
  () => theme.value,
  (t) => applyTheme(t),
);
</script>

<style scoped>
.settings-page {
  max-width: 980px;
  margin: 0 auto;
  padding: 24px;
  font-family:
    system-ui,
    -apple-system,
    Segoe UI,
    Roboto,
    Arial,
    sans-serif;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.subtitle {
  margin: 6px 0 0;
  opacity: 0.75;
}

.panel {
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 14px;
  padding: 16px;
  background: #fff;
}

.mode {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  align-items: stretch;
}

.mode-card {
  text-align: left;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 14px;
  padding: 14px;
  background: rgba(0, 0, 0, 0.02);
  cursor: pointer;
}

.mode-card.active {
  border-color: rgba(0, 0, 0, 0.35);
  background: rgba(0, 0, 0, 0.06);
}

.mode-title {
  font-weight: 600;
  color: #111;
}

.mode-sub {
  margin-top: 6px;
  font-size: 13px;
  opacity: 0.7;
}

.divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.08);
  margin: 16px 0;
}

.content {
  display: grid;
  gap: 14px;
}

.head {
  display: grid;
  gap: 6px;
}

.head-split {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.actions {
  display: flex;
  gap: 10px;
}

.hint {
  margin: 0;
  opacity: 0.7;
  font-size: 13px;
}

.form {
  display: grid;
  gap: 14px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.grid-2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.field label {
  display: block;
  font-size: 13px;
  margin: 0 0 6px;
  opacity: 0.9;
}

.field input,
.field textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 10px;
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.15);
  outline: none;
  background: #fff;
  color: #111;
}

.field textarea {
  resize: vertical;
}

.field input:disabled,
.field textarea:disabled {
  background: rgba(0, 0, 0, 0.04);
  color: rgba(0, 0, 0, 0.65);
  border-color: rgba(0, 0, 0, 0.1);
  cursor: not-allowed;
}

.small {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  opacity: 0.7;
}

.btn {
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.18);
  background: #fff;
  cursor: pointer;
  color: #111;
}

.btn:hover {
  border-color: rgba(0, 0, 0, 0.35);
}

.btn.primary {
  background: rgba(0, 0, 0, 0.1);
  border-color: rgba(0, 0, 0, 0.35);
  font-weight: 600;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.message {
  margin: 0;
  opacity: 0.85;
}

.theme {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.theme-btn {
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.18);
  background: #fff;
  cursor: pointer;
  color: #111;
}

.theme-btn.active {
  background: rgba(0, 0, 0, 0.1);
  border-color: rgba(0, 0, 0, 0.35);
  font-weight: 600;
}

@media (max-width: 900px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .mode {
    grid-template-columns: 1fr;
  }
  .grid {
    grid-template-columns: 1fr;
  }
  .head-split {
    flex-direction: column;
    align-items: stretch;
  }
  .actions {
    justify-content: flex-end;
  }
}
</style>
