<template>
  <div class="settings-layout">

    <!-- ── Sidebar ─────────────────────────────────────────────── -->
    <aside class="settings-sidebar">
      <h1 class="settings-sidebar__title">Einstellungen</h1>
      <nav class="settings-nav">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          class="settings-nav__item"
          :class="{ 'settings-nav__item--active': activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.label }}
        </button>
      </nav>
    </aside>

    <!-- ── Content ─────────────────────────────────────────────── -->
    <main class="settings-content">

      <!-- ════════════════════════════════════
           TAB: Dokumenteinstellungen
      ════════════════════════════════════ -->
      <section v-if="activeTab === 'document'" class="settings-section">
        <div class="section-header">
          <h2 class="section-title">Dokumenteinstellungen</h2>
          <p class="section-desc">Diese Daten werden automatisch als Standardwerte im Creator vorausgefüllt.</p>
        </div>

        <div class="settings-card">

          <!-- Company block -->
          <div class="card-block">
            <h3 class="card-block__title">Unternehmen</h3>
            <div class="field-grid">
              <div class="field-group field-group--full">
                <label>Firmenname</label>
                <input v-model="doc.companyName" type="text" placeholder="Muster GmbH" />
              </div>
              <div class="field-group">
                <label>Straße & Nr.</label>
                <input v-model="doc.companyStreet" type="text" placeholder="Musterstraße 21" />
              </div>
              <div class="field-group">
                <label>PLZ & Stadt</label>
                <input v-model="doc.companyCity" type="text" placeholder="73728 Esslingen" />
              </div>
              <div class="field-group">
                <label>USt-IdNr.</label>
                <input v-model="doc.taxId" type="text" placeholder="DE123456789" />
              </div>
              <div class="field-group">
                <label>Ansprechpartner</label>
                <input v-model="doc.contact" type="text" placeholder="Anna Musterfrau" />
              </div>
            </div>
          </div>

          <div class="card-divider" />

          <!-- Bank block -->
          <div class="card-block">
            <h3 class="card-block__title">Bankverbindung</h3>
            <div class="field-grid">
              <div class="field-group field-group--full">
                <label>Bank</label>
                <input v-model="doc.bankName" type="text" placeholder="Deutsche Bank" />
              </div>
              <div class="field-group">
                <label>IBAN</label>
                <input v-model="doc.iban" type="text" placeholder="DE98 1213 6424 1111 3465 9752" />
              </div>
              <div class="field-group">
                <label>BIC</label>
                <input v-model="doc.bic" type="text" placeholder="DBKEDFHH" />
              </div>
            </div>
          </div>

          <div class="card-divider" />

          <!-- Invoice defaults block -->
          <div class="card-block">
            <h3 class="card-block__title">Rechnungsstandards</h3>
            <div class="field-grid">
              <div class="field-group">
                <label>Standard-MwSt. (%)</label>
                <input v-model.number="doc.defaultTax" type="number" min="0" max="100" placeholder="19" />
              </div>
              <div class="field-group">
                <label>Rechnungsnr.-Format</label>
                <input v-model="doc.invoiceFormat" type="text" placeholder="RE-{YYYY}-{NNN}" />
              </div>
              <div class="field-group field-group--full">
                <label>Standard-Fußzeile</label>
                <input v-model="doc.footerText" type="text" placeholder="Logo oHG | Musterstraße 21 | logo@mail.de" />
              </div>
            </div>
          </div>

        </div>

        <!-- Save button -->
        <div class="save-row">
          <transition name="fade-check" mode="out-in">
            <button
              v-if="!docSaved"
              key="save"
              class="btn-save"
              @click="saveDoc"
            >
              Speichern
            </button>
            <div v-else key="saved" class="btn-saved">
              <Check :size="15" />
              Einstellungen gespeichert
            </div>
          </transition>
        </div>
      </section>

      <!-- ════════════════════════════════════
           TAB: Theme
      ════════════════════════════════════ -->
      <section v-if="activeTab === 'theme'" class="settings-section">
        <div class="section-header">
          <h2 class="section-title">Design</h2>
          <p class="section-desc">Wähle das Erscheinungsbild der Anwendung.</p>
        </div>

        <div class="settings-card">
          <div class="card-block">
            <h3 class="card-block__title">Farbschema</h3>

            <div class="theme-options">
              <button
                v-for="option in themeOptions"
                :key="option.id"
                class="theme-card"
                :class="{ 'theme-card--active': selectedTheme === option.id }"
                @click="selectTheme(option.id)"
              >
                <!-- Visual preview mockup -->
                <div class="theme-card__preview" :class="`theme-card__preview--${option.id}`">
                  <div class="preview-nav"></div>
                  <div class="preview-body">
                    <div class="preview-sidebar"></div>
                    <div class="preview-main">
                      <div class="preview-block"></div>
                      <div class="preview-block preview-block--short"></div>
                    </div>
                  </div>
                </div>

                <div class="theme-card__footer">
                  <span class="theme-card__label">{{ option.label }}</span>
                  <span class="theme-card__check" :class="{ 'theme-card__check--active': selectedTheme === option.id }">
                    <Check :size="12" />
                  </span>
                </div>
              </button>
            </div>
          </div>
        </div>

        <!-- Save button -->
        <div class="save-row">
          <transition name="fade-check" mode="out-in">
            <button
              v-if="!themeSaved"
              key="save"
              class="btn-save"
              @click="saveTheme"
            >
              Speichern
            </button>
            <div v-else key="saved" class="btn-saved">
              <Check :size="15" />
              Einstellungen gespeichert
            </div>
          </transition>
        </div>
      </section>

    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { Check } from 'lucide-vue-next'

// ─── Tabs ─────────────────────────────────────────────────────────────────────

const tabs: Array<{ id: 'document' | 'theme'; label: string }> = [
  { id: 'document', label: 'Einstellungen' },
  { id: 'theme',    label: 'Creator'       },
]

const activeTab = ref<'document' | 'theme'>('document')

// ─── Document settings ────────────────────────────────────────────────────────

const doc = reactive({
  companyName:   '',
  companyStreet: '',
  companyCity:   '',
  taxId:         '',
  contact:       '',
  bankName:      '',
  iban:          '',
  bic:           '',
  defaultTax:    19,
  invoiceFormat: 'RE-{YYYY}-{NNN}',
  footerText:    '',
})

const docSaved = ref(false)
let docTimer: ReturnType<typeof setTimeout>

function saveDoc() {
  // Persist to localStorage so Creator can read on mount
  localStorage.setItem('invar_doc_settings', JSON.stringify({ ...doc }))
  docSaved.value = true
  clearTimeout(docTimer)
  docTimer = setTimeout(() => { docSaved.value = false }, 3000)
}

// Load on mount
const stored = localStorage.getItem('invar_doc_settings')
if (stored) {
  try { Object.assign(doc, JSON.parse(stored)) } catch {  }
}

// ─── Theme settings ───────────────────────────────────────────────────────────

const themeOptions = [
  { id: 'light',  label: 'Hell'   },
  { id: 'dark',   label: 'Dunkel' },
  { id: 'system', label: 'System' },
]

const selectedTheme = ref<'light' | 'dark' | 'system'>('system')
const themeSaved = ref(false)
let themeTimer: ReturnType<typeof setTimeout>

// Load saved theme
const storedTheme = localStorage.getItem('invar_theme')
if (storedTheme === 'light' || storedTheme === 'dark' || storedTheme === 'system') {
  selectedTheme.value = storedTheme
}

function selectTheme(id: 'light' | 'dark' | 'system') {
  selectedTheme.value = id
}

function applyTheme(theme: 'light' | 'dark' | 'system') {
  const root = document.documentElement
  if (theme === 'system') {
    root.removeAttribute('data-theme')
  } else {
    root.setAttribute('data-theme', theme)
  }
}

function saveTheme() {
  localStorage.setItem('invar_theme', selectedTheme.value)
  applyTheme(selectedTheme.value)
  themeSaved.value = true
  clearTimeout(themeTimer)
  themeTimer = setTimeout(() => { themeSaved.value = false }, 3000)
}

// Apply on load
applyTheme(selectedTheme.value)
</script>

<style scoped>
/* ─── Layout ─────────────────────────────────────────────────────────────── */
.settings-layout {
  display: flex;
  min-height: calc(100vh - 64px);
  background: var(--primary-background-color);
  font-family: 'Aspekta', sans-serif;
  color: var(--primary-text-color);
}

/* ─── Sidebar ────────────────────────────────────────────────────────────── */
.settings-sidebar {
  width: 280px;
  min-width: 220px;
  flex-shrink: 0;
  padding: 32px 20px 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: var(--primary-background-color);
}

.settings-sidebar__title {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary-text-color);
  margin: 0;
  padding: 0 8px;
  letter-spacing: -0.3px;
}

/* Nav island */
.settings-nav {
  background: #fff;
  border: 1px solid var(--border-bottom);
    overflow: hidden;
  display: flex;
  flex-direction: column;
}

[data-theme="dark"] .settings-nav {
  background: #1a1a1a;
  border-color: rgba(255,255,255,0.12);
}

.settings-nav__item {
  width: 100%;
  text-align: left;
  background: none;
  border: none;
  border-bottom: 1px solid var(--border-bottom);
  padding: 13px 16px;
  font-family: 'Aspekta', sans-serif;
  font-size: 13px;
  font-weight: 400;
  color: var(--primary-text-color);
  cursor: pointer;
  transition: background 0.13s;
}

.settings-nav__item:last-child {
  border-bottom: none;
}

.settings-nav__item:hover {
  background: var(--highlited);
}

.settings-nav__item--active {
  background: var(--highlited);
  font-weight: 600;
}

/* ─── Content area ───────────────────────────────────────────────────────── */
.settings-content {
  flex: 1;
  padding: 32px 40px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ─── Section wrapper ────────────────────────────────────────────────────── */
.settings-section {
  width: 100%;
  max-width: 1060px;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--border-bottom);
}

/* ─── Each bordered block ────────────────────────────────────────────────── */
.section-header,
.card-block,
.save-row {
  background: #fff;
  border-top: 1px solid var(--border-bottom);
  border-bottom: 1px solid var(--border-bottom);
  margin-top: -1px;
}

[data-theme="dark"] .section-header,
[data-theme="dark"] .card-block,
[data-theme="dark"] .save-row {
  background: #1a1a1a;
  border-color: rgba(255,255,255,0.12);
}

/* ─── Section header block ───────────────────────────────────────────────── */
.section-header {
  padding: 28px 32px 24px;
  margin-top: 0;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.5px;
  margin: 0 0 8px;
  color: var(--primary-text-color);
}

.section-desc {
  font-size: 13px;
  color: var(--primary-text-color);
  opacity: 0.55;
  margin: 0;
}

/* ─── Settings card — now just a wrapper, no own border/shadow ───────────── */
.settings-card {
  display: contents; /* children participate directly in section flow */
}

.card-block {
  padding: 20px 32px 24px;
}

.card-block__title {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-text-color);
  margin: 0 0 18px;
  letter-spacing: 0;
  text-transform: none;
  opacity: 1;
}

/* remove the old explicit divider — borders between blocks handle it */
.card-divider { display: none; }

/* ─── Field grid ─────────────────────────────────────────────────────────── */
.field-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px 20px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-group--full {
  grid-column: 1 / -1;
}

.field-group label {
  font-size: 12px;
  font-weight: 400;
  color: var(--primary-text-color);
  opacity: 0.7;
  text-transform: none;
  letter-spacing: 0;
}

.field-group input {
  border: none;
  padding: 8px 10px;
  font-size: 13px;
  font-family: 'Aspekta', sans-serif;
  color: var(--primary-text-color);
  background: var(--seoncdary-background-color);
  outline: none;
  width: 100%;
  box-sizing: border-box;
  transition: box-shadow 0.14s;
}

.field-group input:focus {
  box-shadow: 0 0 0 2px var(--primary-accent-color);
}

[data-theme="dark"] .field-group input {
  background: #2a2a2a;
  color: #f0f0f0;
}

/* ─── Save row ───────────────────────────────────────────────────────────── */
.save-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 16px 32px;
}

.btn-save {
  padding: 10px 28px;
  background: var(--primary-accent-color);
  color: #fff;
  border: none;
  font-size: 13px;
  font-weight: 600;
  font-family: 'Aspekta', sans-serif;
  cursor: pointer;
  transition: background 0.14s, transform 0.1s;
  letter-spacing: -0.1px;
}

.btn-save:hover  { background: #0520a0; }
.btn-save:active { transform: scale(0.97); }

.btn-saved {
  display: flex;
  align-items: center;
  gap: 7px;
  color: var(--settings-saved);
  font-size: 13px;
  font-weight: 600;
  font-family: 'Aspekta', sans-serif;
}

/* ─── Fade-check transition ──────────────────────────────────────────────── */
.fade-check-enter-active,
.fade-check-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}
.fade-check-enter-from { opacity: 0; transform: translateY(4px); }
.fade-check-leave-to   { opacity: 0; transform: translateY(-4px); }

/* ─── Theme selector ─────────────────────────────────────────────────────── */
.theme-options {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.theme-card {
  background: none;
  border: 1.5px solid var(--seoncdary-background-color);
  padding: 0;
  cursor: pointer;
  overflow: hidden;
  width: 160px;
  transition: border-color 0.15s, box-shadow 0.15s;
  display: flex;
  flex-direction: column;
  font-family: 'Aspekta', sans-serif;
}

.theme-card:hover {
  border-color: rgba(7, 42, 200, 0.4);
}

.theme-card--active {
  border-color: var(--primary-accent-color);
  box-shadow: 0 0 0 1px var(--primary-accent-color);
}

/* Preview mockup */
.theme-card__preview {
  height: 100px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Light preview */
.theme-card__preview--light .preview-nav   { background: #000814; height: 16px; flex-shrink: 0; }
.theme-card__preview--light .preview-body  { display: flex; flex: 1; background: #f5f5f5; }
.theme-card__preview--light .preview-sidebar { width: 36px; background: #e2e2e2; flex-shrink: 0; }
.theme-card__preview--light .preview-main  { flex: 1; padding: 8px 6px; display: flex; flex-direction: column; gap: 4px; }
.theme-card__preview--light .preview-block { background: #fff; height: 14px; border-radius: 2px; }
.theme-card__preview--light .preview-block--short { width: 60%; }

/* Dark preview */
.theme-card__preview--dark .preview-nav   { background: #000; height: 16px; flex-shrink: 0; }
.theme-card__preview--dark .preview-body  { display: flex; flex: 1; background: #111; }
.theme-card__preview--dark .preview-sidebar { width: 36px; background: #1e1e1e; flex-shrink: 0; }
.theme-card__preview--dark .preview-main  { flex: 1; padding: 8px 6px; display: flex; flex-direction: column; gap: 4px; }
.theme-card__preview--dark .preview-block { background: #2a2a2a; height: 14px; border-radius: 2px; }
.theme-card__preview--dark .preview-block--short { width: 60%; }

/* System preview — split half/half */
.theme-card__preview--system {
  display: grid;
  grid-template-columns: 1fr 1fr;
}
.theme-card__preview--system .preview-nav    { display: none; }
.theme-card__preview--system .preview-body   { display: contents; }
.theme-card__preview--system .preview-sidebar { display: none; }
.theme-card__preview--system .preview-main   { display: none; }

/* custom split via ::before / ::after on the preview element itself */
.theme-card__preview--system::before {
  content: '';
  display: block;
  background: #f5f5f5;
  height: 100%;
}
.theme-card__preview--system::after {
  content: '';
  display: block;
  background: #111;
  height: 100%;
}

/* Footer row inside card */
.theme-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-top: 1px solid var(--seoncdary-background-color);
  background: var(--primary-background-color);
}

[data-theme="dark"] .theme-card__footer {
  background: #1a1a1a;
  border-top-color: rgba(255,255,255,0.1);
}

.theme-card__label {
  font-size: 12px;
  font-weight: 600;
  color: var(--primary-text-color);
}

.theme-card__check {
  width: 18px;
  height: 18px;
  border: 1.5px solid var(--seoncdary-background-color);
  display: flex;
  align-items: center;
  justify-content: center;
  color: transparent;
  transition: background 0.14s, border-color 0.14s, color 0.14s;
  flex-shrink: 0;
}

.theme-card__check--active {
  background: var(--primary-accent-color);
  border-color: var(--primary-accent-color);
  color: #fff;
}

/* ─── Responsive ─────────────────────────────────────────────────────────── */
@media (max-width: 900px) {
  .field-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .settings-layout {
    flex-direction: column;
  }

  .settings-sidebar {
    width: 100%;
    padding: 20px 16px 12px;
    flex-direction: row;
    align-items: center;
    gap: 16px;
  }

  .settings-sidebar__title {
    flex-shrink: 0;
  }

  .settings-nav {
    flex-direction: row;
    overflow-x: auto;
    border-radius: 6px;
    flex: 1;
  }

  .settings-nav__item {
    border-bottom: none;
    border-right: 1px solid var(--border-bottom);
    white-space: nowrap;
    flex-shrink: 0;
  }

  .settings-nav__item:last-child {
    border-right: none;
  }

  .settings-content {
    padding: 16px;
  }

  .section-header,
  .card-block,
  .save-row {
    padding-left: 20px;
    padding-right: 20px;
  }

  .field-grid {
    grid-template-columns: 1fr;
  }

  .field-group--full {
    grid-column: 1;
  }

  .theme-options { gap: 12px; }
  .theme-card { width: 130px; }
}
</style>
