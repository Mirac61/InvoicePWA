<template>
  <div class="creator-layout">
    <!-- LEFT PANEL: Editor -->
    <aside class="editor-panel">
      <div class="editor-header">
        <h2 class="editor-title">Creator</h2>
        <span class="section-count">{{ sections.length }} Abschnitte</span>
      </div>

      <!-- Draggable Sections -->
      <div class="sections-list">
        <div
          v-for="(section, index) in sections"
          :key="section.id"
          class="section-card"
          :class="{ 'section-card--active': activeSection === section.id }"
          draggable="true"
          @dragstart="onDragStart($event, index)"
          @dragover.prevent="onDragOver($event, index)"
          @drop="onDrop($event, index)"
          @dragend="onDragEnd"
        >
          <div class="section-card__header" @click="toggleSection(section.id)">
            <div class="section-card__left">
              <span class="drag-handle">⠿</span>
              <span class="section-icon">{{ section.icon }}</span>
              <span class="section-name">{{ section.label }}</span>
            </div>
            <div class="section-card__right">
              <button
                class="btn-icon btn-icon--danger"
                @click.stop="removeSection(section.id)"
                title="Entfernen"
              >✕</button>
              <span class="chevron" :class="{ 'chevron--open': activeSection === section.id }">›</span>
            </div>
          </div>

          <!-- Section Content -->
          <transition name="slide">
            <div v-if="activeSection === section.id" class="section-card__body">

              <!-- HEADER SECTION -->
              <template v-if="section.type === 'header'">
                <div class="field-group">
                  <label>Firmen-Logo</label>
                  <div class="logo-upload" @click="triggerLogoUpload">
                    <img v-if="invoiceData.logo" :src="invoiceData.logo" class="logo-preview" alt="Logo" />
                    <template v-else>
                      <span class="logo-upload__icon">↑</span>
                      <span>Datei auswählen</span>
                    </template>
                    <input ref="logoInput" type="file" accept="image/*" style="display:none" @change="onLogoUpload" />
                  </div>
                  <span v-if="invoiceData.logo" class="logo-filename" @click="invoiceData.logo = ''">✕ Logo entfernen</span>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Firmenname</label>
                    <input v-model="invoiceData.companyName" type="text" placeholder="Muster GmbH" />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Straße & Nr.</label>
                    <input v-model="invoiceData.companyStreet" type="text" placeholder="Musterstraße 21" />
                  </div>
                  <div class="field-group">
                    <label>PLZ & Stadt</label>
                    <input v-model="invoiceData.companyCity" type="text" placeholder="73728 Esslingen" />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>USt-IdNr.</label>
                    <input v-model="invoiceData.taxId" type="text" placeholder="DE123456789" />
                  </div>
                  <div class="field-group">
                    <label>Ansprechpartner</label>
                    <input v-model="invoiceData.contact" type="text" placeholder="Anna Musterfrau" />
                  </div>
                </div>
              </template>

              <!-- CUSTOMER SECTION -->
              <template v-if="section.type === 'customer'">
                <div class="field-row">
                  <div class="field-group">
                    <label>Kunden-Name</label>
                    <input v-model="invoiceData.customerName" type="text" placeholder="Max Mustermann" />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Straße & Nr.</label>
                    <input v-model="invoiceData.customerStreet" type="text" placeholder="Berliner Straße 41" />
                  </div>
                  <div class="field-group">
                    <label>PLZ & Stadt</label>
                    <input v-model="invoiceData.customerCity" type="text" placeholder="12450 Musterstadt" />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Kunden-Nr.</label>
                    <input v-model="invoiceData.customerId" type="text" placeholder="469332" />
                  </div>
                </div>
              </template>

              <!-- META SECTION -->
              <template v-if="section.type === 'meta'">
                <div class="field-row">
                  <div class="field-group">
                    <label>Rechnungs-Nr.</label>
                    <input v-model="invoiceData.invoiceNumber" type="text" placeholder="10230445" />
                  </div>
                  <div class="field-group">
                    <label>Rechnungsdatum</label>
                    <input v-model="invoiceData.invoiceDate" type="date" />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Fälligkeitsdatum</label>
                    <input v-model="invoiceData.dueDate" type="date" />
                  </div>
                </div>
                <div class="field-group">
                  <label>Anrede / Einleitung</label>
                  <textarea v-model="invoiceData.intro" rows="3" placeholder="Sehr geehrter Herr Mustermann,\nvielen Dank für Ihren Auftrag..."></textarea>
                </div>
              </template>

              <!-- TABLE SECTION -->
              <template v-if="section.type === 'table'">
                <div class="table-builder">
                  <div class="table-builder__columns">
                    <label>Spalten konfigurieren</label>
                    <div class="column-tags">
                      <div
                        v-for="(col, ci) in invoiceData.tableColumns"
                        :key="ci"
                        class="column-tag"
                      >
                        <input v-model="col.label" type="text" class="column-tag__input" />
                        <select v-model="col.align" class="column-tag__align">
                          <option value="left">L</option>
                          <option value="center">C</option>
                          <option value="right">R</option>
                        </select>
                        <button class="column-tag__remove" @click="removeColumn(ci)">✕</button>
                      </div>
                      <button class="btn-add-col" @click="addColumn">+ Spalte</button>
                    </div>
                  </div>

                  <div class="table-rows-editor">
                    <label>Positionen</label>
                    <div
                      v-for="(row, ri) in invoiceData.tableRows"
                      :key="ri"
                      class="table-row-editor"
                    >
                      <span class="row-num">{{ ri + 1 }}</span>
                      <div class="row-cells">
                        <input
                          v-for="(col, ci) in invoiceData.tableColumns"
                          :key="ci"
                          v-model="row.cells[ci]"
                          type="text"
                          :placeholder="col.label"
                          class="row-cell-input"
                        />
                      </div>
                      <button class="btn-icon btn-icon--danger" @click="removeRow(ri)">✕</button>
                    </div>
                    <button class="btn-add-row" @click="addRow">+ Position hinzufügen</button>
                  </div>

                  <div class="totals-config">
                    <label>Summen</label>
                    <div class="field-row">
                      <div class="field-group">
                        <label>MwSt. (%)</label>
                        <input v-model.number="invoiceData.taxRate" type="number" min="0" max="100" placeholder="19" />
                      </div>
                      <div class="field-group">
                        <label>Rabatt (%)</label>
                        <input v-model.number="invoiceData.discountRate" type="number" min="0" max="100" placeholder="0" />
                      </div>
                    </div>
                    <div class="toggle-row">
                      <label class="toggle-label">
                        <input type="checkbox" v-model="invoiceData.showSubtotal" />
                        Zwischensumme anzeigen
                      </label>
                      <label class="toggle-label">
                        <input type="checkbox" v-model="invoiceData.showTax" />
                        MwSt. anzeigen
                      </label>
                    </div>
                  </div>
                </div>
              </template>

              <!-- BANKING SECTION -->
              <template v-if="section.type === 'banking'">
                <div class="field-row">
                  <div class="field-group">
                    <label>Bank</label>
                    <input v-model="invoiceData.bankName" type="text" placeholder="Deutsche Bank" />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>IBAN</label>
                    <input v-model="invoiceData.iban" type="text" placeholder="DE98 1213 6424 1111 3465 9752" />
                  </div>
                  <div class="field-group">
                    <label>BIC</label>
                    <input v-model="invoiceData.bic" type="text" placeholder="DBKEDFHH" />
                  </div>
                </div>
                <div class="field-group">
                  <label>Zahlungsziel (Text)</label>
                  <input v-model="invoiceData.paymentNote" type="text" placeholder="Bitte überweisen Sie den Betrag bis zum..." />
                </div>
              </template>

              <!-- FOOTER SECTION -->
              <template v-if="section.type === 'footer'">
                <div class="field-group">
                  <label>Schlusstext</label>
                  <textarea v-model="invoiceData.closingText" rows="2" placeholder="Wir bedanken uns für Ihren Auftrag."></textarea>
                </div>
                <div class="field-group">
                  <label>Fußzeile</label>
                  <input v-model="invoiceData.footerText" type="text" placeholder="Logo oHG | Musterstraße 21 | Tel: ..." />
                </div>
              </template>

            </div>
          </transition>
        </div>
      </div>

      <!-- Add Section -->
      <div class="add-section-area">
        <p class="add-section-label">Abschnitt hinzufügen</p>
        <div class="add-section-buttons">
          <button
            v-for="avail in availableSectionsToAdd"
            :key="avail.type"
            class="btn-add-section"
            @click="addSection(avail.type)"
          >
            <span>{{ avail.icon }}</span> {{ avail.label }}
          </button>
        </div>
      </div>
    </aside>

    <!-- RIGHT PANEL: Preview -->
    <main class="preview-panel">
      <div class="preview-header">
        <h2 class="editor-title">Vorschau</h2>
        <div class="preview-actions">
          <select v-model="selectedTemplate" class="template-select">
            <option value="classic">Klassisch</option>
            <option value="modern">Modern</option>
            <option value="minimal">Minimal</option>
          </select>
          <button class="btn-download" @click="downloadPDF">
            <span>↓</span> Als PDF herunterladen
          </button>
        </div>
      </div>

      <div class="preview-wrapper">
        <div class="preview-scale-container">
          <div
            id="invoice-preview"
            class="invoice-preview"
            :class="`template--${selectedTemplate}`"
          >
            <!-- Render sections in order -->
            <template v-for="section in sections" :key="section.id">

              <!-- HEADER -->
              <div v-if="section.type === 'header'" class="inv-header">
                <div class="inv-header__logo">
                  <img v-if="invoiceData.logo" :src="invoiceData.logo" alt="Logo" class="inv-logo-img" />
                  <div v-else class="inv-logo-placeholder">
                    <span class="inv-logo-dot"></span>
                    <span class="inv-logo-text">{{ invoiceData.companyName || 'Logo' }}</span>
                  </div>
                </div>
                <div class="inv-header__company">
                  <strong>{{ invoiceData.companyName || 'Firmenname GmbH' }}</strong>
                  <span>{{ invoiceData.companyStreet }}</span>
                  <span>{{ invoiceData.companyCity }}</span>
                </div>
              </div>

              <!-- CUSTOMER + META -->
              <div v-if="section.type === 'customer'" class="inv-addresses">
                <div class="inv-from">
                  <p>{{ invoiceData.customerName }}</p>
                  <p>{{ invoiceData.customerStreet }}</p>
                  <p>{{ invoiceData.customerCity }}</p>
                </div>
              </div>

              <!-- META -->
              <div v-if="section.type === 'meta'" class="inv-meta">
                <div class="inv-meta__left">
                  <p v-if="invoiceData.invoiceNumber"><span>Rechnungs-Nr.:</span> {{ invoiceData.invoiceNumber }}</p>
                  <p v-if="invoiceData.customerId"><span>Kunden-Nr.:</span> {{ invoiceData.customerId }}</p>
                  <p v-if="invoiceData.invoiceDate"><span>Rechnungsdatum:</span> {{ formatDate(invoiceData.invoiceDate) }}</p>
                  <p v-if="invoiceData.dueDate"><span>Fälligkeitsdatum:</span> {{ formatDate(invoiceData.dueDate) }}</p>
                </div>
                <div v-if="invoiceData.intro" class="inv-intro">
                  <p v-if="invoiceData.invoiceNumber" class="inv-title">Ihre Rechnung</p>
                  <p>{{ invoiceData.intro }}</p>
                </div>
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
                      >{{ col.label }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(row, ri) in invoiceData.tableRows" :key="ri">
                      <td
                        v-for="(col, ci) in invoiceData.tableColumns"
                        :key="ci"
                        :style="{ textAlign: col.align }"
                      >{{ row.cells[ci] || '' }}</td>
                    </tr>
                  </tbody>
                </table>

                <!-- Totals -->
                <div class="inv-totals">
                  <div v-if="invoiceData.showSubtotal && invoiceData.discountRate > 0" class="inv-totals__row inv-totals__row--sub">
                    <span>Zwischensumme</span>
                    <span>{{ formatCurrency(subtotal) }}</span>
                  </div>
                  <div v-if="invoiceData.discountRate > 0" class="inv-totals__row inv-totals__row--discount">
                    <span>Rabatt ({{ invoiceData.discountRate }}%)</span>
                    <span>-{{ formatCurrency(discountAmount) }}</span>
                  </div>
                  <div v-if="invoiceData.showTax && invoiceData.taxRate > 0" class="inv-totals__row">
                    <span>MwSt. ({{ invoiceData.taxRate }}%)</span>
                    <span>{{ formatCurrency(taxAmount) }}</span>
                  </div>
                  <div class="inv-totals__row inv-totals__row--total">
                    <span>Gesamtsumme</span>
                    <span>{{ formatCurrency(total) }}</span>
                  </div>
                </div>
              </div>

              <!-- BANKING -->
              <div v-if="section.type === 'banking'" class="inv-banking">
                <div class="inv-banking__left">
                  <p>{{ invoiceData.companyName }}</p>
                  <p>{{ invoiceData.companyStreet }}</p>
                  <p>{{ invoiceData.companyCity }}</p>
                  <p v-if="invoiceData.taxId">USt-IdNr.: {{ invoiceData.taxId }}</p>
                  <p v-if="invoiceData.contact">Ansprechpartner: {{ invoiceData.contact }}</p>
                </div>
                <div class="inv-banking__right">
                  <p>{{ invoiceData.bankName }}</p>
                  <p v-if="invoiceData.iban">IBAN: {{ invoiceData.iban }}</p>
                  <p v-if="invoiceData.bic">BIC: {{ invoiceData.bic }}</p>
                </div>
              </div>

              <!-- FOOTER -->
              <div v-if="section.type === 'footer'" class="inv-footer">
                <p v-if="invoiceData.paymentNote" class="inv-payment-note">{{ invoiceData.paymentNote }}</p>
                <p v-if="invoiceData.closingText" class="inv-closing">{{ invoiceData.closingText }}</p>
                <div v-if="invoiceData.footerText" class="inv-footer-bar">
                  <p>{{ invoiceData.footerText }}</p>
                </div>
              </div>

            </template>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick } from 'vue'

// ─── Types ───────────────────────────────────────────────────────────────────

interface TableColumn {
  label: string
  align: 'left' | 'center' | 'right'
}

interface TableRow {
  cells: string[]
}

interface InvoiceData {
  logo: string
  companyName: string
  companyStreet: string
  companyCity: string
  taxId: string
  contact: string
  customerName: string
  customerStreet: string
  customerCity: string
  customerId: string
  invoiceNumber: string
  invoiceDate: string
  dueDate: string
  intro: string
  tableColumns: TableColumn[]
  tableRows: TableRow[]
  taxRate: number
  discountRate: number
  showSubtotal: boolean
  showTax: boolean
  bankName: string
  iban: string
  bic: string
  paymentNote: string
  closingText: string
  footerText: string
}

interface Section {
  id: string
  type: string
  label: string
  icon: string
}

// ─── State ───────────────────────────────────────────────────────────────────

const selectedTemplate = ref<'classic' | 'modern' | 'minimal'>('classic')
const activeSection = ref<string | null>(null)
const logoInput = ref<HTMLInputElement | null>(null)

const ALL_SECTION_TYPES: Omit<Section, 'id'>[] = [
  { type: 'header',   label: 'Firmen-Header',  icon: '🏢' },
  { type: 'customer', label: 'Kundendaten',     icon: '👤' },
  { type: 'meta',     label: 'Rechnungsinfos',  icon: '📋' },
  { type: 'table',    label: 'Positionen',      icon: '📊' },
  { type: 'banking',  label: 'Bankverbindung',  icon: '🏦' },
  { type: 'footer',   label: 'Abschluss',       icon: '✉️' },
]

let idCounter = 0
const makeId = () => `s-${++idCounter}`

const sections = ref<Section[]>([
  { id: makeId(), type: 'header',   label: 'Firmen-Header', icon: '🏢' },
  { id: makeId(), type: 'customer', label: 'Kundendaten',   icon: '👤' },
  { id: makeId(), type: 'meta',     label: 'Rechnungsinfos',icon: '📋' },
  { id: makeId(), type: 'table',    label: 'Positionen',    icon: '📊' },
  { id: makeId(), type: 'banking',  label: 'Bankverbindung',icon: '🏦' },
  { id: makeId(), type: 'footer',   label: 'Abschluss',     icon: '✉️' },
])

const invoiceData = reactive<InvoiceData>({
  logo: '',
  companyName: 'Logo oHG',
  companyStreet: 'Musterstraße 21',
  companyCity: '73728 Esslingen am Neckar',
  taxId: 'DE124356789',
  contact: 'Anna Musterfrau',
  customerName: 'Mustermann Hafengesellschaft GmbH',
  customerStreet: 'Hafenbezirk 1',
  customerCity: '21079 Hamburg',
  customerId: '469332',
  invoiceNumber: '10230445',
  invoiceDate: '2026-03-14',
  dueDate: '2026-03-29',
  intro: '',
  tableColumns: [
    { label: 'Position',       align: 'left'  },
    { label: 'Leistung',       align: 'left'  },
    { label: 'Beschreibung',   align: 'left'  },
    { label: 'Anzahl/Aufwand', align: 'right' },
    { label: 'Summe',          align: 'right' },
  ],
  tableRows: [
    { cells: ['001', 'Prozessanalyse', 'Optimierung der Container-Abwicklung (Terminal 2)', '12 Std.', '1440,00€'] },
    { cells: ['002', 'Sicherheits-Check', 'Jährliche Unterweisung Hafensicherheit (DGUV)', '1 Pauschale', '850,00€'] },
    { cells: ['003', 'Fahrtkosten', 'Anfahrt Hamburg – Hafenbezirk 0,60 €/km', '690km', '414,00€'] },
  ],
  taxRate: 19,
  discountRate: 10,
  showSubtotal: true,
  showTax: false,
  bankName: 'Deutsche Bank',
  iban: 'DE98 1213 6424 1111 3465 9752',
  bic: 'DBKEDFHH',
  paymentNote: 'Bitte überweisen Sie den Rechnungsbetrag unter Angabe der Rechnungsnummer.',
  closingText: 'Wir bedanken uns für Ihren Auftrag.',
  footerText: 'Logo oHG | Musterstraße 21, 73728 Esslingen am Neckar | logo@mail.de | Tel.: 0737 281204',
})

// ─── Drag & Drop ─────────────────────────────────────────────────────────────

const dragIndex = ref<number | null>(null)
const dragOverIndex = ref<number | null>(null)

function onDragStart(e: DragEvent, index: number) {
  dragIndex.value = index
  if (e.dataTransfer) e.dataTransfer.effectAllowed = 'move'
}

function onDragOver(e: DragEvent, index: number) {
  dragOverIndex.value = index
  if (e.dataTransfer) e.dataTransfer.dropEffect = 'move'
}

function onDrop(e: DragEvent, toIndex: number) {
  const from = dragIndex.value
  if (from === null || from === toIndex) return
  const arr = [...sections.value]
  const [moved] = arr.splice(from, 1)
  arr.splice(toIndex, 0, moved)
  sections.value = arr
  dragIndex.value = null
  dragOverIndex.value = null
}

function onDragEnd() {
  dragIndex.value = null
  dragOverIndex.value = null
}

// ─── Section Management ───────────────────────────────────────────────────────

function toggleSection(id: string) {
  activeSection.value = activeSection.value === id ? null : id
}

function removeSection(id: string) {
  sections.value = sections.value.filter(s => s.id !== id)
  if (activeSection.value === id) activeSection.value = null
}

const availableSectionsToAdd = computed(() =>
  ALL_SECTION_TYPES.filter(t => !sections.value.some(s => s.type === t.type))
)

function addSection(type: string) {
  const tmpl = ALL_SECTION_TYPES.find(t => t.type === type)
  if (!tmpl) return
  const newSection: Section = { id: makeId(), ...tmpl }
  sections.value.push(newSection)
  nextTick(() => { activeSection.value = newSection.id })
}

// ─── Table Builder ────────────────────────────────────────────────────────────

function addColumn() {
  invoiceData.tableColumns.push({ label: 'Neue Spalte', align: 'left' })
  invoiceData.tableRows.forEach(row => row.cells.push(''))
}

function removeColumn(ci: number) {
  invoiceData.tableColumns.splice(ci, 1)
  invoiceData.tableRows.forEach(row => row.cells.splice(ci, 1))
}

function addRow() {
  invoiceData.tableRows.push({ cells: invoiceData.tableColumns.map(() => '') })
}

function removeRow(ri: number) {
  invoiceData.tableRows.splice(ri, 1)
}

// ─── Logo Upload ──────────────────────────────────────────────────────────────

function triggerLogoUpload() {
  logoInput.value?.click()
}

function onLogoUpload(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = ev => { invoiceData.logo = ev.target?.result as string }
  reader.readAsDataURL(file)
}

// ─── Computeds (Totals) ───────────────────────────────────────────────────────

const subtotal = computed(() => {
  // Try to parse last column as currency values
  return invoiceData.tableRows.reduce((sum, row) => {
    const lastCell = row.cells[row.cells.length - 1] || ''
    const num = parseFloat(lastCell.replace(/[^\d,.-]/g, '').replace(',', '.')) || 0
    return sum + num
  }, 0)
})

const discountAmount = computed(() => subtotal.value * (invoiceData.discountRate / 100))
const afterDiscount = computed(() => subtotal.value - discountAmount.value)
const taxAmount = computed(() => afterDiscount.value * (invoiceData.taxRate / 100))
const total = computed(() => afterDiscount.value + (invoiceData.showTax ? taxAmount.value : 0))

// ─── Helpers ──────────────────────────────────────────────────────────────────

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const [y, m, d] = dateStr.split('-')
  return `${d}.${m}.${y}`
}

function formatCurrency(val: number): string {
  return val.toLocaleString('de-DE', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + '€'
}

// ─── PDF Download ─────────────────────────────────────────────────────────────

async function downloadPDF() {
  // Uses browser print dialog with print-specific CSS to generate PDF
  const printBtn = document.querySelector('.btn-download') as HTMLElement
  if (printBtn) printBtn.textContent = 'Wird vorbereitet...'
  
  await nextTick()
  
  // Add print class to only show invoice
  document.body.classList.add('printing')
  window.print()
  document.body.classList.remove('printing')
  
  if (printBtn) {
    printBtn.innerHTML = '<span>↓</span> Als PDF herunterladen'
  }
}
</script>

<style scoped>
/* ─── Layout ──────────────────────────────────────────────────────────────── */

.creator-layout {
  display: flex;
  height: calc(100vh - 64px);
  overflow: hidden;
  background: #f4f4f2;
  font-family: 'DM Sans', 'Helvetica Neue', sans-serif;
}

/* ─── Editor Panel ─────────────────────────────────────────────────────────── */

.editor-panel {
  width: 420px;
  min-width: 320px;
  max-width: 480px;
  background: #fff;
  border-right: 1px solid #e8e8e4;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 24px 24px 16px;
  border-bottom: 1px solid #f0f0ec;
  flex-shrink: 0;
}

.editor-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: -0.5px;
  color: #111;
  margin: 0;
}

.section-count {
  font-size: 12px;
  color: #999;
  font-weight: 400;
}

.sections-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.sections-list::-webkit-scrollbar { width: 4px; }
.sections-list::-webkit-scrollbar-track { background: transparent; }
.sections-list::-webkit-scrollbar-thumb { background: #ddd; border-radius: 2px; }

/* ─── Section Card ─────────────────────────────────────────────────────────── */

.section-card {
  background: #fafaf8;
  border: 1px solid #e8e8e4;
  border-radius: 10px;
  overflow: hidden;
  cursor: default;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.section-card:hover {
  border-color: #d0d0cc;
}

.section-card--active {
  border-color: #111;
  box-shadow: 0 0 0 1px #111;
}

.section-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  cursor: pointer;
  user-select: none;
}

.section-card__left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-card__right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.drag-handle {
  color: #bbb;
  cursor: grab;
  font-size: 16px;
  line-height: 1;
}

.drag-handle:active { cursor: grabbing; }

.section-icon {
  font-size: 14px;
}

.section-name {
  font-size: 13px;
  font-weight: 500;
  color: #222;
}

.chevron {
  font-size: 16px;
  color: #999;
  transform: rotate(0deg);
  transition: transform 0.2s;
  line-height: 1;
  display: inline-block;
}

.chevron--open {
  transform: rotate(90deg);
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 11px;
  color: #aaa;
  transition: color 0.15s, background 0.15s;
}

.btn-icon--danger:hover {
  color: #e53e3e;
  background: #fff5f5;
}

.section-card__body {
  padding: 4px 14px 16px;
  border-top: 1px solid #f0f0ec;
}

/* ─── Slide transition ─────────────────────────────────────────────────────── */

.slide-enter-active,
.slide-leave-active {
  transition: all 0.2s ease;
  overflow: hidden;
}

.slide-enter-from,
.slide-leave-to {
  max-height: 0;
  opacity: 0;
}

.slide-enter-to,
.slide-leave-from {
  max-height: 600px;
  opacity: 1;
}

/* ─── Form Fields ──────────────────────────────────────────────────────────── */

.field-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 10px;
}

.field-group label {
  font-size: 11px;
  font-weight: 500;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.field-row {
  display: flex;
  gap: 10px;
}

.field-row .field-group {
  flex: 1;
}

input[type="text"],
input[type="date"],
input[type="number"],
textarea,
select {
  border: 1px solid #e0e0dc;
  border-radius: 6px;
  padding: 7px 10px;
  font-size: 13px;
  font-family: inherit;
  color: #222;
  background: #fff;
  width: 100%;
  box-sizing: border-box;
  outline: none;
  transition: border-color 0.15s;
}

input:focus,
textarea:focus,
select:focus {
  border-color: #111;
}

textarea {
  resize: vertical;
  min-height: 60px;
}

/* ─── Logo Upload ──────────────────────────────────────────────────────────── */

.logo-upload {
  border: 1.5px dashed #ddd;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  font-size: 12px;
  color: #888;
  transition: border-color 0.15s, background 0.15s;
  min-height: 56px;
}

.logo-upload:hover {
  border-color: #111;
  background: #fafaf8;
}

.logo-upload__icon {
  font-size: 18px;
}

.logo-preview {
  max-height: 44px;
  max-width: 160px;
  object-fit: contain;
}

.logo-filename {
  font-size: 11px;
  color: #e53e3e;
  cursor: pointer;
  margin-top: 4px;
}

/* ─── Table Builder ────────────────────────────────────────────────────────── */

.table-builder {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.table-builder__columns label,
.table-rows-editor label,
.totals-config label {
  font-size: 11px;
  font-weight: 500;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: block;
  margin-bottom: 6px;
}

.column-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}

.column-tag {
  display: flex;
  align-items: center;
  gap: 3px;
  background: #f4f4f2;
  border: 1px solid #e0e0dc;
  border-radius: 6px;
  padding: 3px 6px;
}

.column-tag__input {
  border: none;
  background: transparent;
  padding: 2px 4px;
  width: 90px;
  font-size: 12px;
  color: #333;
}

.column-tag__input:focus {
  border-color: transparent;
  outline: none;
}

.column-tag__align {
  border: none;
  background: transparent;
  font-size: 11px;
  padding: 0 2px;
  color: #888;
  cursor: pointer;
}

.column-tag__remove {
  background: none;
  border: none;
  color: #bbb;
  cursor: pointer;
  font-size: 10px;
  padding: 0 2px;
}

.column-tag__remove:hover { color: #e53e3e; }

.btn-add-col {
  font-size: 12px;
  color: #666;
  background: #f4f4f2;
  border: 1.5px dashed #ddd;
  border-radius: 6px;
  padding: 4px 10px;
  cursor: pointer;
  transition: all 0.15s;
}

.btn-add-col:hover { border-color: #111; color: #111; }

.table-row-editor {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0ec;
}

.row-num {
  font-size: 11px;
  color: #bbb;
  min-width: 20px;
  text-align: center;
}

.row-cells {
  display: flex;
  flex: 1;
  gap: 4px;
  flex-wrap: wrap;
}

.row-cell-input {
  flex: 1;
  min-width: 60px;
  font-size: 12px;
  padding: 5px 7px;
}

.btn-add-row {
  margin-top: 8px;
  width: 100%;
  padding: 8px;
  background: none;
  border: 1.5px dashed #ddd;
  border-radius: 6px;
  font-size: 12px;
  color: #888;
  cursor: pointer;
  transition: all 0.15s;
}

.btn-add-row:hover { border-color: #111; color: #111; }

.totals-config { }

.toggle-row {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.toggle-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #555;
  cursor: pointer;
}

.toggle-label input[type="checkbox"] {
  width: auto;
  cursor: pointer;
}

/* ─── Add Section ──────────────────────────────────────────────────────────── */

.add-section-area {
  padding: 12px 16px 20px;
  border-top: 1px solid #f0f0ec;
  flex-shrink: 0;
}

.add-section-label {
  font-size: 11px;
  font-weight: 500;
  color: #aaa;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin: 0 0 8px;
}

.add-section-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.btn-add-section {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
  background: #f4f4f2;
  border: 1px solid #e4e4e0;
  border-radius: 6px;
  font-size: 12px;
  color: #555;
  cursor: pointer;
  transition: all 0.15s;
}

.btn-add-section:hover {
  background: #111;
  color: #fff;
  border-color: #111;
}

/* ─── Preview Panel ────────────────────────────────────────────────────────── */

.preview-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f4f4f2;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 28px 16px;
  flex-shrink: 0;
}

.preview-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.template-select {
  width: auto;
  font-size: 13px;
  padding: 7px 12px;
  border-radius: 8px;
  cursor: pointer;
}

.btn-download {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  background: #1a56db;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, transform 0.1s;
}

.btn-download:hover { background: #1447b8; }
.btn-download:active { transform: scale(0.97); }

.preview-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 0 28px 28px;
  display: flex;
  justify-content: center;
}

.preview-wrapper::-webkit-scrollbar { width: 4px; }
.preview-wrapper::-webkit-scrollbar-track { background: transparent; }
.preview-wrapper::-webkit-scrollbar-thumb { background: #ccc; border-radius: 2px; }

.preview-scale-container {
  width: 100%;
  max-width: 680px;
}

/* ─── Invoice Preview ──────────────────────────────────────────────────────── */

.invoice-preview {
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 20px rgba(0,0,0,0.08);
  padding: 52px 56px;
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 11px;
  color: #222;
  line-height: 1.5;
  min-height: 900px;
  position: relative;
}

/* Header */
.inv-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.inv-header__logo {
  display: flex;
  align-items: center;
}

.inv-logo-placeholder {
  display: flex;
  align-items: center;
  gap: 8px;
}

.inv-logo-dot {
  width: 20px;
  height: 20px;
  background: #1a56db;
  border-radius: 50%;
  display: inline-block;
}

.inv-logo-text {
  font-size: 14px;
  font-weight: 600;
  color: #111;
}

.inv-logo-img {
  max-height: 40px;
  max-width: 120px;
  object-fit: contain;
}

.inv-header__company {
  display: flex;
  flex-direction: column;
  text-align: right;
  font-size: 10.5px;
  color: #555;
  line-height: 1.6;
}

.inv-header__company strong {
  color: #111;
  font-weight: 600;
  font-size: 11px;
}

/* Addresses */
.inv-addresses {
  margin-bottom: 20px;
}

.inv-from {
  font-size: 10.5px;
  color: #444;
  line-height: 1.6;
}

/* Meta */
.inv-meta {
  margin-bottom: 28px;
}

.inv-meta__left {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 1px 12px;
  font-size: 10.5px;
  color: #555;
  margin-bottom: 16px;
}

.inv-meta__left p {
  display: contents;
  margin: 0;
}

.inv-meta__left span {
  color: #888;
  font-size: 10px;
}

.inv-intro {
  margin-top: 16px;
  font-size: 10.5px;
  color: #444;
}

.inv-title {
  font-size: 20px !important;
  font-weight: 700;
  color: #111 !important;
  margin-bottom: 8px !important;
  letter-spacing: -0.5px;
}

/* Table */
.inv-table-section {
  margin-bottom: 28px;
}

.inv-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 10px;
}

.inv-table thead tr {
  background: #f0f0ec;
}

.inv-table thead th {
  padding: 7px 8px;
  font-weight: 600;
  font-size: 9.5px;
  text-transform: uppercase;
  letter-spacing: 0.3px;
  color: #555;
  border: none;
}

.inv-table tbody tr {
  border-bottom: 1px solid #f4f4f2;
}

.inv-table tbody td {
  padding: 8px 8px;
  color: #333;
  vertical-align: top;
}

/* Totals */
.inv-totals {
  margin-top: 2px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.inv-totals__row {
  display: flex;
  justify-content: flex-end;
  gap: 32px;
  padding: 5px 8px;
  font-size: 10px;
  color: #555;
  width: 280px;
}

.inv-totals__row--sub {
  background: #f4f4f2;
  font-weight: 500;
}

.inv-totals__row--discount {
  color: #e53e3e;
}

.inv-totals__row--total {
  background: #f0f0ec;
  font-weight: 700;
  font-size: 11px;
  color: #111;
}

/* Banking */
.inv-banking {
  display: flex;
  justify-content: space-between;
  padding-top: 28px;
  margin-top: 28px;
  border-top: 1px solid #e8e8e4;
  font-size: 10px;
  color: #555;
  line-height: 1.7;
}

/* Footer */
.inv-footer {
  margin-top: 24px;
  font-size: 10px;
  color: #555;
}

.inv-payment-note {
  margin-bottom: 8px;
}

.inv-closing {
  text-align: center;
  margin: 20px 0;
  color: #555;
}

.inv-footer-bar {
  border-top: 1px solid #e8e8e4;
  padding-top: 10px;
  text-align: center;
  color: #999;
  font-size: 9px;
  margin-top: 20px;
}

/* ─── Template Variants ─────────────────────────────────────────────────────── */

.template--modern .inv-table thead tr {
  background: #111;
}

.template--modern .inv-table thead th {
  color: #fff;
}

.template--modern .inv-totals__row--total {
  background: #111;
  color: #fff;
}

.template--modern .inv-logo-dot {
  border-radius: 2px;
  background: #111;
}

.template--minimal .inv-header {
  border-bottom: 2px solid #111;
  padding-bottom: 16px;
}

.template--minimal .inv-table thead tr {
  background: transparent;
  border-bottom: 2px solid #111;
}

.template--minimal .inv-table thead th {
  color: #111;
  background: transparent;
}

.template--minimal .inv-totals__row--total {
  background: transparent;
  border-top: 2px solid #111;
}

.template--minimal .inv-logo-dot {
  background: #111;
}

/* ─── Responsive ─────────────────────────────────────────────────────────────── */

@media (max-width: 900px) {
  .creator-layout {
    flex-direction: column;
    height: auto;
    overflow: auto;
  }

  .editor-panel {
    width: 100%;
    max-width: 100%;
    height: auto;
    overflow: visible;
  }

  .sections-list {
    overflow: visible;
  }

  .preview-panel {
    height: auto;
    overflow: visible;
  }

  .preview-wrapper {
    overflow: visible;
    padding: 0 16px 28px;
  }
}

@media (max-width: 600px) {
  .invoice-preview {
    padding: 28px 24px;
  }

  .field-row {
    flex-direction: column;
  }

  .preview-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}

/* ─── Print Styles ─────────────────────────────────────────────────────────── */
</style>