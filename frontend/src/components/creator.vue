<template>
  <div class="creator-layout">
    <!-- ═══════════════════════════════════════════════════════════
         LEFT PANEL — Editor
    ════════════════════════════════════════════════════════════ -->
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
          :class="{
            'section-card--active': activeSection === section.id,
            'section-card--dragging': dragIndex === index,
            'section-card--dragover':
              dragOverIndex === index && dragIndex !== index,
          }"
          draggable="true"
          @dragstart="onDragStart($event, index)"
          @dragover.prevent="onDragOver($event, index)"
          @drop="onDrop($event, index)"
          @dragend="onDragEnd"
        >
          <!-- Card Header -->
          <div class="section-card__header" @click="toggleSection(section.id)">
            <div class="section-card__left">
              <span class="drag-handle" @click.stop>⠿</span>
              <span class="section-name">{{ section.label }}</span>
            </div>
            <div class="section-card__right">
              <button
                class="btn-icon btn-icon--danger"
                @click.stop="removeSection(section.id)"
                title="Entfernen"
              >
                ✕
              </button>
              <span
                class="chevron"
                :class="{ 'chevron--open': activeSection === section.id }"
                >›</span
              >
            </div>
          </div>

          <!-- Card Body -->
          <transition name="slide">
            <div v-if="activeSection === section.id" class="section-card__body">
              <!-- ── HEADER / Unternehmen ─────────────────────────── -->
              <template v-if="section.type === 'header'">
                <div class="field-group">
                  <label>Bild des Logos hochladen</label>
                  <div
                    class="logo-upload"
                    :class="{
                      'logo-upload--has-file': !!invoiceData.logo,
                      'logo-upload--dragover': logoDragOver,
                    }"
                    @click="triggerLogoUpload"
                    @dragover.prevent="logoDragOver = true"
                    @dragleave="logoDragOver = false"
                    @drop.prevent="onLogoDrop"
                  >
                    <img
                      v-if="invoiceData.logo"
                      :src="invoiceData.logo"
                      class="logo-preview"
                      alt="Firmenlogo"
                    />
                    <template v-else>
                      <span class="logo-upload__arrow">↑</span>
                      <span class="logo-upload__label">
                        <button class="logo-upload__btn" type="button">
                          Datei auswählen
                        </button>
                        <span v-if="invoiceData.logoName"
                          >&nbsp;{{ invoiceData.logoName }}</span
                        >
                        <span v-else class="logo-upload__hint"
                          >&nbsp;oder hierher ziehen</span
                        >
                      </span>
                    </template>
                    <input
                      ref="logoInputRef"
                      type="file"
                      accept="image/png,image/jpeg,image/jpg,image/svg+xml,image/webp"
                      style="display: none"
                      @change="onLogoFileChange"
                    />
                  </div>
                  <button
                    v-if="invoiceData.logo"
                    class="logo-remove"
                    type="button"
                    @click.stop="
                      invoiceData.logo = '';
                      invoiceData.logoName = '';
                    "
                  >
                    ✕ Logo entfernen
                  </button>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Firmenname</label>
                    <input
                      v-model="invoiceData.companyName"
                      type="text"
                      placeholder="Muster GmbH"
                    />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Straße & Nr.</label>
                    <input
                      v-model="invoiceData.companyStreet"
                      type="text"
                      placeholder="Musterstraße 21"
                    />
                  </div>
                  <div class="field-group">
                    <label>PLZ & Stadt</label>
                    <input
                      v-model="invoiceData.companyCity"
                      type="text"
                      placeholder="73728 Esslingen"
                    />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>USt-IdNr.</label>
                    <input
                      v-model="invoiceData.taxId"
                      type="text"
                      placeholder="DE123456789"
                    />
                  </div>
                  <div class="field-group">
                    <label>Ansprechpartner</label>
                    <input
                      v-model="invoiceData.contact"
                      type="text"
                      placeholder="Anna Musterfrau"
                    />
                  </div>
                </div>
              </template>

              <!-- ── CUSTOMER / Kundendaten ─────────────────────── -->
              <template v-if="section.type === 'customer'">
                <!-- Customer live search -->
                <div class="field-group customer-search-wrap">
                  <label>Kundenname</label>
                  <input
                    v-model="customerSearch"
                    type="text"
                    placeholder="z.B. Mustermann..."
                    autocomplete="off"
                    @input="onCustomerSearchInput"
                    @blur="onCustomerSearchBlur"
                  />

                  <!-- Dropdown Ergebnisse -->
                  <div
                    v-if="customerSuggestions.length > 0"
                    class="customer-dropdown"
                  >
                    <button
                      v-for="c in customerSuggestions"
                      :key="c.id"
                      class="customer-suggestion-item"
                      type="button"
                      @mousedown.prevent="selectCustomer(c)"
                    >
                      <span class="suggestion-name"
                        >{{ c.forename }} {{ c.surname }}</span
                      >
                      <span class="suggestion-meta"
                        >{{ c.street }}, {{ c.zip }} {{ c.city }}</span
                      >
                    </button>
                  </div>

                  <!-- Zustands-Hinweise unterhalb des Feldes -->
                  <span v-if="customerSearchLoading" class="search-hint"
                    >Suche läuft…</span
                  >
                  <span
                    v-else-if="selectedCustomerObj"
                    class="search-hint search-hint--ok"
                    >✓ Kunde verknüpft</span
                  >

                  <!-- "Nicht gefunden" – erscheint wenn getippt, keine Treffer, kein Loading -->
                  <div
                    v-else-if="
                      customerSearch.length >= 2 &&
                      !customerSearchLoading &&
                      customerSuggestions.length === 0 &&
                      !selectedCustomerObj
                    "
                    class="customer-not-found"
                  >
                    <span class="not-found-text">Kein Kunde gefunden.</span>
                    <button
                      class="btn-create-inline"
                      type="button"
                      @click="openNewCustomerModal"
                    >
                      Neu erstellen?
                    </button>
                  </div>
                </div>

                <div class="field-row">
                  <div class="field-group">
                    <label>Straße und Nummer</label>
                    <input
                      v-model="invoiceData.customerStreet"
                      type="text"
                      placeholder="Berliner Straße 41"
                    />
                  </div>
                  <div class="field-group">
                    <label>PLZ, Stadt</label>
                    <input
                      v-model="invoiceData.customerCity"
                      type="text"
                      placeholder="12450 Musterstadt"
                    />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Kundennummer</label>
                    <input
                      v-model="invoiceData.customerId"
                      type="text"
                      placeholder="469332"
                      readonly
                      style="background: #f9f9f7; color: #aaa"
                    />
                  </div>
                </div>
              </template>

              <!-- ── META / Rechnungsinfo ────────────────────────── -->
              <template v-if="section.type === 'meta'">
                <div class="field-row">
                  <div class="field-group">
                    <label>Rechnungsnummer</label>
                    <input
                      v-model="invoiceData.invoiceNumber"
                      type="text"
                      placeholder="10230445"
                    />
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
                <button class="btn-add-entry" type="button">
                  + Weiterer Eintrag
                </button>
              </template>

              <!-- ── GREETING / Anrede ───────────────────────────── -->
              <template v-if="section.type === 'greeting'">
                <div class="field-group">
                  <label>Rechnungsnummer</label>
                  <textarea
                    v-model="invoiceData.intro"
                    rows="5"
                    placeholder="Sehr geehrte/r Frau/Herr Mustermann, vielen dank..."
                  ></textarea>
                </div>
              </template>

              <!-- ── TABLE / Tabelle ────────────────────────────── -->
              <template v-if="section.type === 'table'">
                <!-- Column configurator -->
                <div class="table-section-group">
                  <span class="table-section-label">Spalten konfigurieren</span>
                  <div class="column-list">
                    <div
                      v-for="(col, ci) in invoiceData.tableColumns"
                      :key="ci"
                      class="column-row"
                    >
                      <input
                        v-model="col.label"
                        type="text"
                        class="column-name-input"
                        placeholder="Spaltenname"
                      />
                      <select v-model="col.align" class="column-align-select">
                        <option value="left">L</option>
                        <option value="center">M</option>
                        <option value="right">R</option>
                      </select>
                      <button
                        class="btn-icon btn-icon--danger"
                        @click="removeColumn(ci)"
                      >
                        ✕
                      </button>
                    </div>
                  </div>
                  <button class="btn-add-col" type="button" @click="addColumn">
                    + Spalte
                  </button>
                </div>

                <!-- Row editor -->
                <div class="table-section-group">
                  <span class="table-section-label">Zeileneinträge</span>
                  <div
                    v-for="(row, ri) in invoiceData.tableRows"
                    :key="ri"
                    class="row-editor"
                  >
                    <span class="row-num">{{ ri + 1 }}</span>
                    <div class="row-cells">
                      <template
                        v-for="(col, ci) in invoiceData.tableColumns"
                        :key="ci"
                      >
                        <!-- Item-search on the title column (Leistung/Name/Titel), plain input for all others -->
                        <div
                          v-if="isTitleColumn(ci)"
                          class="row-cell-search-wrap"
                        >
                          <input
                            v-model="row.cells[ci]"
                            type="text"
                            :placeholder="col.label"
                            class="row-cell-input"
                            autocomplete="off"
                            @input="onItemSearchInput(ri, row.cells[ci])"
                            @blur="onItemSearchBlur(ri)"
                          />
                          <!-- Item dropdown -->
                          <div
                            v-if="itemSuggestions[ri]?.length > 0"
                            class="item-dropdown"
                          >
                            <button
                              v-for="item in itemSuggestions[ri]"
                              :key="item.id"
                              class="item-suggestion-item"
                              type="button"
                              @mousedown.prevent="selectItem(ri, item)"
                            >
                              <span class="suggestion-name">{{
                                item.name
                              }}</span>
                              <span class="suggestion-meta"
                                >{{ formatCurrency(item.unitPriceNet) }} · MwSt
                                {{
                                  (item.defaultTaxRate * 100).toFixed(0)
                                }}%<span v-if="item.defaultUnit">
                                  · {{ item.defaultUnit }}</span
                                ></span
                              >
                            </button>
                          </div>
                          <!-- Not found hint -->
                          <div
                            v-else-if="
                              (row.cells[ci] ?? '').length >= 2 &&
                              !itemSearchLoading[ri] &&
                              itemSuggestions[ri]?.length === 0 &&
                              !row.linkedItemId
                            "
                            class="customer-not-found"
                          >
                            <span class="not-found-text">Nicht gefunden.</span>
                            <button
                              class="btn-create-inline"
                              type="button"
                              @mousedown.prevent="openNewItemModal(ri)"
                            >
                              Neu erstellen?
                            </button>
                          </div>
                          <span
                            v-if="row.linkedItemId"
                            class="search-hint search-hint--ok"
                            style="font-size: 10px"
                            >✓ Artikel verknüpft</span
                          >
                        </div>
                        <input
                          v-else
                          v-model="row.cells[ci]"
                          type="text"
                          :placeholder="col.label"
                          class="row-cell-input"
                        />
                      </template>
                    </div>
                    <button
                      class="btn-icon btn-icon--danger"
                      @click="removeRow(ri)"
                    >
                      ✕
                    </button>
                  </div>
                  <button class="btn-add-row" type="button" @click="addRow">
                    + Position hinzufügen
                  </button>
                </div>

                <!-- Totals configuration -->
                <div class="table-section-group">
                  <span class="table-section-label">Summen</span>
                  <div class="field-row">
                    <div class="field-group">
                      <label>MwSt. (%)</label>
                      <input
                        v-model.number="invoiceData.taxRate"
                        type="number"
                        min="0"
                        max="100"
                        placeholder="19"
                      />
                    </div>
                    <div class="field-group">
                      <label>Rabatt (%)</label>
                      <input
                        v-model.number="invoiceData.discountRate"
                        type="number"
                        min="0"
                        max="100"
                        placeholder="0"
                      />
                    </div>
                  </div>
                  <div class="toggle-grid">
                    <label class="toggle-label">
                      <input
                        type="checkbox"
                        v-model="invoiceData.showSubtotal"
                      />
                      Zwischensumme anzeigen
                    </label>
                    <label class="toggle-label">
                      <input type="checkbox" v-model="invoiceData.showTax" />
                      MwSt. anzeigen
                    </label>
                    <label class="toggle-label">
                      <input
                        type="checkbox"
                        v-model="invoiceData.showDiscount"
                      />
                      Rabatte anzeigen
                    </label>
                  </div>
                </div>
              </template>

              <!-- ── BANKING / Bankverbindung ───────────────────── -->
              <template v-if="section.type === 'banking'">
                <div class="field-row">
                  <div class="field-group">
                    <label>Bank</label>
                    <input
                      v-model="invoiceData.bankName"
                      type="text"
                      placeholder="Deutsche Bank"
                    />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>IBAN</label>
                    <input
                      v-model="invoiceData.iban"
                      type="text"
                      placeholder="DE98 1213 6424 1111 3465 9752"
                    />
                  </div>
                  <div class="field-group">
                    <label>BIC</label>
                    <input
                      v-model="invoiceData.bic"
                      type="text"
                      placeholder="DBKEDFHH"
                    />
                  </div>
                </div>
                <div class="field-row">
                  <div class="field-group">
                    <label>Zahlungsziel (Text)</label>
                    <input
                      v-model="invoiceData.paymentNote"
                      type="text"
                      placeholder="Bitte überweisen Sie den Betrag bis zum..."
                    />
                  </div>
                </div>
              </template>

              <!-- ── FOOTER / Abschluss ─────────────────────────── -->
              <template v-if="section.type === 'footer'">
                <div class="field-group">
                  <label>Schlusstext</label>
                  <textarea
                    v-model="invoiceData.closingText"
                    rows="3"
                    placeholder="Wir bedanken uns für Ihren Auftrag."
                  ></textarea>
                </div>
                <div class="field-group">
                  <label>Fußzeile</label>
                  <input
                    v-model="invoiceData.footerText"
                    type="text"
                    placeholder="Logo oHG | Musterstraße 21 | Tel: ..."
                  />
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
            type="button"
            @click="addSection(avail.type)"
          >
            {{ avail.label }}
          </button>
        </div>
      </div>
    </aside>

    <!-- ═══════════════════════════════════════════════════════════
         RIGHT PANEL — Preview
    ════════════════════════════════════════════════════════════ -->
    <main class="preview-panel">
      <div class="preview-header">
        <h2 class="editor-title">Vorschau</h2>
        <div class="preview-actions">
          <select v-model="selectedTemplate" class="template-select">
            <option value="classic">Klassisch</option>
            <option value="modern">Modern</option>
            <option value="minimal">Minimal</option>
          </select>
        </div>
      </div>

      <div class="preview-wrapper">
        <div class="preview-scale-container">
          <div
            id="invoice-preview"
            class="invoice-preview"
            :class="`template--${selectedTemplate}`"
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

                <!-- Totals -->
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
          <!-- /invoice-preview -->
        </div>
      </div>

      <!-- Sticky download bar -->
      <div class="download-bar">
        <button
          class="btn-save"
          type="button"
          @click="saveInvoice"
          :disabled="isSaving"
        >
          <span v-if="isSaving">⏳ Speichern…</span>
          <span v-else>💾 In DB speichern</span>
        </button>
        <button class="btn-download" type="button" @click="downloadPDF">
          <Download :size="16" />
          Als PDF herunterladen
        </button>
      </div>
    </main>

    <!-- ═══════════════════════════════════════════════════════════
         NEW CUSTOMER MODAL
    ════════════════════════════════════════════════════════════ -->
    <Teleport to="body">
      <div
        v-if="showNewCustomerModal"
        class="modal-backdrop"
        @click.self="showNewCustomerModal = false"
      >
        <div class="modal-box">
          <div class="modal-header">
            <h3 class="modal-title">Neuen Kunden anlegen</h3>
            <button
              class="modal-close"
              type="button"
              @click="showNewCustomerModal = false"
            >
              ✕
            </button>
          </div>
          <div class="modal-body">
            <div class="field-row">
              <div class="field-group">
                <label>Typ</label>
                <select v-model="newCustomer.type">
                  <option value="PRIVATE">Privatperson</option>
                  <option value="BUSINESS">Unternehmen</option>
                </select>
              </div>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Vorname *</label>
                <input
                  v-model="newCustomer.forename"
                  type="text"
                  placeholder="Max"
                />
              </div>
              <div class="field-group">
                <label>Nachname *</label>
                <input
                  v-model="newCustomer.surname"
                  type="text"
                  placeholder="Mustermann"
                />
              </div>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Straße & Nr. *</label>
                <input
                  v-model="newCustomer.street"
                  type="text"
                  placeholder="Musterstraße 1"
                />
              </div>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>PLZ *</label>
                <input
                  v-model="newCustomer.zip"
                  type="text"
                  placeholder="12345"
                />
              </div>
              <div class="field-group">
                <label>Stadt *</label>
                <input
                  v-model="newCustomer.city"
                  type="text"
                  placeholder="Berlin"
                />
              </div>
              <div class="field-group">
                <label>Land</label>
                <input
                  v-model="newCustomer.country"
                  type="text"
                  placeholder="DE"
                  maxlength="2"
                />
              </div>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>E-Mail *</label>
                <input
                  v-model="newCustomer.email"
                  type="email"
                  placeholder="max@example.com"
                />
              </div>
              <div class="field-group">
                <label>Telefon *</label>
                <input
                  v-model="newCustomer.phone"
                  type="text"
                  placeholder="+49 30 123456"
                />
              </div>
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>USt-IdNr.</label>
                <input
                  v-model="newCustomer.vatId"
                  type="text"
                  placeholder="DE123456789"
                />
              </div>
            </div>
            <p v-if="newCustomerError" class="modal-error">
              {{ newCustomerError }}
            </p>
          </div>
          <div class="modal-footer">
            <button
              class="btn-modal-cancel"
              type="button"
              @click="showNewCustomerModal = false"
            >
              Abbrechen
            </button>
            <button
              class="btn-modal-save"
              type="button"
              @click="createNewCustomer"
              :disabled="isCreatingCustomer"
            >
              {{ isCreatingCustomer ? "Wird gespeichert…" : "Kunden anlegen" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ═══════════════════════════════════════════════════════════
         NEW ITEM MODAL
    ════════════════════════════════════════════════════════════ -->
    <Teleport to="body">
      <div
        v-if="showNewItemModal"
        class="modal-backdrop"
        @click.self="showNewItemModal = false"
      >
        <div class="modal-box">
          <div class="modal-header">
            <h3 class="modal-title">Neuen Artikel anlegen</h3>
            <button
              class="modal-close"
              type="button"
              @click="showNewItemModal = false"
            >
              ✕
            </button>
          </div>
          <div class="modal-body">
            <div class="field-group">
              <label>Name *</label>
              <input
                v-model="newItem.name"
                type="text"
                placeholder="z.B. Beratungsstunde"
              />
            </div>
            <div class="field-group">
              <label>Beschreibung</label>
              <input
                v-model="newItem.description"
                type="text"
                placeholder="Kurze Beschreibung (optional)"
              />
            </div>
            <div class="field-row">
              <div class="field-group">
                <label>Netto-Preis *</label>
                <input
                  v-model.number="newItem.unitPriceNet"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                />
              </div>
              <div class="field-group">
                <label>MwSt. (%)</label>
                <input
                  v-model.number="newItem.defaultTaxRatePercent"
                  type="number"
                  min="0"
                  max="100"
                  placeholder="19"
                />
              </div>
              <div class="field-group">
                <label>Einheit</label>
                <input
                  v-model="newItem.defaultUnit"
                  type="text"
                  placeholder="h / Stk / pcs"
                />
              </div>
            </div>
            <p v-if="newItemError" class="modal-error">{{ newItemError }}</p>
          </div>
          <div class="modal-footer">
            <button
              class="btn-modal-cancel"
              type="button"
              @click="showNewItemModal = false"
            >
              Abbrechen
            </button>
            <button
              class="btn-modal-save"
              type="button"
              @click="createNewItem"
              :disabled="isCreatingItem"
            >
              {{ isCreatingItem ? "Wird gespeichert…" : "Artikel anlegen" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick } from "vue";
import { Download } from "lucide-vue-next";

// ─── API ─────────────────────────────────────────────────────────────────────
const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";

// ─── Types ────────────────────────────────────────────────────────────────────

interface TableColumn {
  label: string;
  align: "left" | "center" | "right";
}
interface TableRow {
  cells: string[];
  linkedItemId?: string | null;
}

interface InvoiceData {
  logo: string;
  logoName: string;
  companyName: string;
  companyStreet: string;
  companyCity: string;
  taxId: string;
  contact: string;
  customerName: string;
  customerStreet: string;
  customerCity: string;
  customerId: string;
  invoiceNumber: string;
  invoiceDate: string;
  dueDate: string;
  intro: string;
  tableColumns: TableColumn[];
  tableRows: TableRow[];
  taxRate: number;
  discountRate: number;
  showSubtotal: boolean;
  showTax: boolean;
  showDiscount: boolean;
  bankName: string;
  iban: string;
  bic: string;
  paymentNote: string;
  closingText: string;
  footerText: string;
}

interface SectionDef {
  id: string;
  type: string;
  label: string;
  icon: string;
}

// ─── All possible sections ────────────────────────────────────────────────────

const ALL_SECTION_TYPES: Omit<SectionDef, "id">[] = [
  { type: "header", label: "Unternehmen", icon: "" },
  { type: "customer", label: "Kundendaten", icon: "" },
  { type: "meta", label: "Rechnungsinfo", icon: "" },
  { type: "greeting", label: "Anrede", icon: "" },
  { type: "table", label: "Tabelle", icon: "" },
  { type: "banking", label: "Bankverbindung", icon: "" },
  { type: "footer", label: "Abschluss", icon: "" },
];

let _id = 0;
const uid = () => `s-${++_id}`;

// ─── State ────────────────────────────────────────────────────────────────────

const selectedTemplate = ref<"classic" | "modern" | "minimal">("classic");
const activeSection = ref<string | null>(null);
const logoInputRef = ref<HTMLInputElement | null>(null);
const logoDragOver = ref(false);

const sections = ref<SectionDef[]>([
  { id: uid(), type: "header", label: "Unternehmen", icon: "" },
  { id: uid(), type: "customer", label: "Kundendaten", icon: "" },
  { id: uid(), type: "meta", label: "Rechnungsinfo", icon: "" },
  { id: uid(), type: "greeting", label: "Anrede", icon: "" },
  { id: uid(), type: "table", label: "Tabelle", icon: "" },
  { id: uid(), type: "banking", label: "Bankverbindung", icon: "" },
  { id: uid(), type: "footer", label: "Abschluss", icon: "" },
]);

// Example data for preview and testing
const invoiceData = reactive<InvoiceData>({
  logo: "",
  logoName: "",
  companyName: "Logo oHG",
  companyStreet: "Musterstraße 21",
  companyCity: "73728 Esslingen am Neckar",
  taxId: "DE124356789",
  contact: "Anna Musterfrau",
  customerName: "Mustermann Hafengesellschaft GmbH",
  customerStreet: "Hafenbezirk 1",
  customerCity: "21079 Hamburg",
  customerId: "469332",
  invoiceNumber: "10230445",
  invoiceDate: "2026-03-14",
  dueDate: "2026-03-29",
  intro: "",
  tableColumns: [
    { label: "Position", align: "left" },
    { label: "Leistung", align: "left" },
    { label: "Beschreibung", align: "left" },
    { label: "Anzahl/Aufwand", align: "right" },
    { label: "Summe", align: "right" },
  ],
  tableRows: [
    {
      cells: [
        "001",
        "Prozessanalyse",
        "Optimierung der Container-Abwicklung (Terminal 2)",
        "12 Std.",
        "1440,00€",
      ],
    },
    {
      cells: [
        "002",
        "Sicherheits-Check",
        "Jährliche Unterweisung Hafensicherheit (DGUV)",
        "1 Pauschale",
        "850,00€",
      ],
    },
    {
      cells: [
        "003",
        "Fahrtkosten",
        "Anfahrt Hamburg – Hafenbezirk 0,60 €/km",
        "690km",
        "414,00€",
      ],
    },
  ],
  taxRate: 19,
  discountRate: 10,
  showSubtotal: true,
  showTax: false,
  showDiscount: true,
  bankName: "Deutsche Bank",
  iban: "DE98 1213 6424 1111 3465 9752",
  bic: "DBKEDFHH",
  paymentNote:
    "Bitte überweisen Sie den Rechnungsbetrag unter Angabe der Rechnungsnummer.",
  closingText: "Wir bedanken uns für Ihren Auftrag.",
  footerText:
    "Logo oHG | Musterstraße 21, 73728 Esslingen am Neckar | logo@mail.de | Tel.: 0737 281204",
});

const saved = localStorage.getItem("invar_doc_settings");
if (saved) Object.assign(invoiceData, JSON.parse(saved));

// ─── Drag & Drop ─────────────────────────────────────────────────────────────

const dragIndex = ref<number | null>(null);
const dragOverIndex = ref<number | null>(null);

// ─── Customer Search (API) ─────────────────────────────────────────────────
interface CustomerDTO {
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
const customerSearch = ref("");
const customerSuggestions = ref<CustomerDTO[]>([]);
const customerSearchLoading = ref(false);
const selectedCustomerObj = ref<CustomerDTO | null>(null);
let searchTimer: ReturnType<typeof setTimeout> | null = null;

async function fetchCustomers(query: string) {
  customerSearchLoading.value = true;
  try {
    const res = await fetch(
      `${API_BASE}/customers?search=${encodeURIComponent(query)}`,
    );
    if (!res.ok) throw new Error("Fehler beim Laden der Kunden");
    const data: CustomerDTO[] = await res.json();
    // filter client-side in case backend doesn't support ?search
    const q = query.toLowerCase();
    customerSuggestions.value = data
      .filter((c) =>
        `${c.forename} ${c.surname} ${c.email}`.toLowerCase().includes(q),
      )
      .slice(0, 6);
  } catch {
    customerSuggestions.value = [];
  } finally {
    customerSearchLoading.value = false;
  }
}

function onCustomerSearchInput() {
  selectedCustomerObj.value = null;
  if (searchTimer) clearTimeout(searchTimer);
  if (customerSearch.value.length < 2) {
    customerSuggestions.value = [];
    return;
  }
  searchTimer = setTimeout(() => fetchCustomers(customerSearch.value), 280);
}

function onCustomerSearchBlur() {
  setTimeout(() => {
    customerSuggestions.value = [];
  }, 150);
}

function selectCustomer(c: CustomerDTO) {
  selectedCustomerObj.value = c;
  customerSearch.value = `${c.forename} ${c.surname}`;
  customerSuggestions.value = [];
  // Fill fields
  invoiceData.customerName = `${c.forename} ${c.surname}`;
  invoiceData.customerStreet = c.street;
  invoiceData.customerCity = `${c.zip} ${c.city}`;
  invoiceData.customerId = c.id;
}

// ─── Save to DB ───────────────────────────────────────────────────────────────
const isSaving = ref(false);
const saveError = ref<string | null>(null);
const saveSuccessMsg = ref<string | null>(null);

async function saveInvoice() {
  saveError.value = null;
  saveSuccessMsg.value = null;

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

  // Detect columns by label heuristics — same logic as selectItem
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
  // Price: prefer "Summe" or last numeric-looking column
  const priceIdx = labels.findLastIndex(
    (l) =>
      l.includes("sum") ||
      l.includes("preis") ||
      l.includes("price") ||
      l.includes("betrag"),
  );

  // Fallbacks
  const effectiveTitleIdx = titleIdx >= 0 ? titleIdx : 0;
  const effectivePriceIdx = priceIdx >= 0 ? priceIdx : cols.length - 1;
  const effectiveQtyIdx =
    qtyIdx >= 0 ? qtyIdx : cols.length >= 2 ? cols.length - 2 : 0;

  const items = invoiceData.tableRows
    .filter((row) => row.cells.some((c) => c?.trim())) // skip fully empty rows
    .map((row, idx) => {
      const priceRaw = (row.cells[effectivePriceIdx] ?? "")
        .replace(/[^\d,.-]/g, "")
        .replace(",", ".");
      const qtyRaw = (row.cells[effectiveQtyIdx] ?? "")
        .replace(/[^\d,.]/g, "")
        .replace(",", ".");

      const price = parseFloat(priceRaw) || 0;
      const qty = parseFloat(qtyRaw) || 1;
      // "Summe" column = gross price for qty items → derive unit price
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

  console.log("[saveInvoice] payload:", JSON.stringify(payload, null, 2));

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
      console.error("[saveInvoice] server error:", err);
      throw new Error(err.message ?? `HTTP ${res.status}`);
    }

    const created = await res.json();
    invoiceData.invoiceNumber = created.invoiceNumber;
    saveSuccessMsg.value = `Rechnung ${created.invoiceNumber} erfolgreich gespeichert!`;
    alert(`✅ ${saveSuccessMsg.value}`);
  } catch (e: any) {
    saveError.value = e.message;
    alert(`❌ Fehler beim Speichern: ${e.message}`);
  } finally {
    isSaving.value = false;
  }
}

// ─── New Customer Modal ────────────────────────────────────────────────────
const showNewCustomerModal = ref(false);
const isCreatingCustomer = ref(false);
const newCustomerError = ref<string | null>(null);

const newCustomer = reactive({
  type: "PRIVATE",
  forename: "",
  surname: "",
  street: "",
  zip: "",
  city: "",
  country: "DE",
  email: "",
  phone: "",
  vatId: "",
});

function openNewCustomerModal() {
  // Pre-fill name from search input
  const parts = customerSearch.value.trim().split(/\s+/);
  newCustomer.forename = parts[0] ?? "";
  newCustomer.surname = parts.slice(1).join(" ");
  newCustomer.street = "";
  newCustomer.zip = "";
  newCustomer.city = "";
  newCustomer.country = "DE";
  newCustomer.email = "";
  newCustomer.phone = "";
  newCustomer.vatId = "";
  newCustomer.type = "PRIVATE";
  newCustomerError.value = null;
  showNewCustomerModal.value = true;
}

async function createNewCustomer() {
  newCustomerError.value = null;
  if (!newCustomer.forename.trim()) {
    newCustomerError.value = "Vorname ist erforderlich.";
    return;
  }
  if (!newCustomer.surname.trim()) {
    newCustomerError.value = "Nachname ist erforderlich.";
    return;
  }
  if (!newCustomer.street.trim()) {
    newCustomerError.value = "Straße ist erforderlich.";
    return;
  }
  if (!newCustomer.zip.trim()) {
    newCustomerError.value = "PLZ ist erforderlich.";
    return;
  }
  if (!newCustomer.city.trim()) {
    newCustomerError.value = "Stadt ist erforderlich.";
    return;
  }
  if (!newCustomer.email.trim()) {
    newCustomerError.value = "E-Mail ist erforderlich.";
    return;
  }
  if (!newCustomer.phone.trim()) {
    newCustomerError.value = "Telefon ist erforderlich.";
    return;
  }

  isCreatingCustomer.value = true;
  try {
    const res = await fetch(`${API_BASE}/customers`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        type: newCustomer.type,
        forename: newCustomer.forename.trim(),
        surname: newCustomer.surname.trim(),
        street: newCustomer.street.trim(),
        zip: newCustomer.zip.trim(),
        city: newCustomer.city.trim(),
        country: newCustomer.country.trim() || "DE",
        email: newCustomer.email.trim() || null,
        phone: newCustomer.phone.trim() || null,
        vatId: newCustomer.vatId.trim() || null,
      }),
    });

    if (!res.ok) {
      const err = await res
        .json()
        .catch(() => ({ message: `HTTP ${res.status}` }));
      throw new Error(err.message ?? `HTTP ${res.status}`);
    }

    const created: CustomerDTO = await res.json();
    selectCustomer(created);
    showNewCustomerModal.value = false;
  } catch (e: any) {
    newCustomerError.value = `Fehler: ${e.message}`;
  } finally {
    isCreatingCustomer.value = false;
  }
}

// ─── Item Search (per row) ─────────────────────────────────────────────────
interface ItemDTO {
  id: string;
  name: string;
  description: string | null;
  defaultUnit: string | null;
  defaultTaxRate: number;
  unitPriceNet: number;
  active: boolean;
}

// Per-row state
const itemSuggestions = ref<Record<number, ItemDTO[]>>({});
const itemSearchLoading = ref<Record<number, boolean>>({});
const itemSearchTimers = ref<Record<number, ReturnType<typeof setTimeout>>>({});

// Extend TableRow to carry optional linked item id
// (rows already exist as { cells: string[] }, we add linkedItemId dynamically)

let allItemsCache: ItemDTO[] | null = null;

async function fetchAllItems() {
  if (allItemsCache) return allItemsCache;
  try {
    const res = await fetch(`${API_BASE}/items`);
    if (!res.ok) throw new Error();
    allItemsCache = await res.json();
  } catch {
    allItemsCache = [];
  }
  return allItemsCache!;
}

async function searchItems(query: string): Promise<ItemDTO[]> {
  const all = await fetchAllItems();
  const q = query.toLowerCase();
  return all
    .filter((i) => i.active && i.name.toLowerCase().includes(q))
    .slice(0, 6);
}

// Returns true for the column that should have item-search (Leistung/Name/Titel)
// Falls back to index 1 if no match found (skip "Position" at index 0)
function isTitleColumn(ci: number): boolean {
  const labels = invoiceData.tableColumns.map((c) => c.label.toLowerCase());
  const titleIdx = labels.findIndex(
    (l) =>
      l.includes("leist") ||
      l.includes("name") ||
      l.includes("titel") ||
      l.includes("artikel"),
  );
  if (titleIdx >= 0) return ci === titleIdx;
  // fallback: if first column looks like position/nr, use second; otherwise first
  const firstIsPos =
    labels[0]?.includes("pos") ||
    labels[0]?.includes("nr") ||
    labels[0]?.includes("num");
  return firstIsPos ? ci === 1 : ci === 0;
}

function onItemSearchInput(ri: number, value: string) {
  // Clear linked item if user edits
  const row = invoiceData.tableRows[ri] as any;
  if (row.linkedItemId) row.linkedItemId = null;

  if (itemSearchTimers.value[ri]) clearTimeout(itemSearchTimers.value[ri]);
  if (!value || value.length < 2) {
    itemSuggestions.value[ri] = [];
    return;
  }

  itemSearchLoading.value[ri] = true;
  itemSearchTimers.value[ri] = setTimeout(async () => {
    itemSuggestions.value[ri] = await searchItems(value);
    itemSearchLoading.value[ri] = false;
  }, 250);
}

function onItemSearchBlur(ri: number) {
  setTimeout(() => {
    itemSuggestions.value[ri] = [];
  }, 160);
}

function selectItem(ri: number, item: ItemDTO) {
  const row = invoiceData.tableRows[ri] as any;
  const cols = invoiceData.tableColumns;
  const labels = cols.map((c) => c.label.toLowerCase());

  // Find title column via isTitleColumn helper
  const titleColIdx = cols.findIndex((_, i) => isTitleColumn(i));
  const effectiveTitleIdx = titleColIdx >= 0 ? titleColIdx : 0;

  row.cells[effectiveTitleIdx] = item.name;

  const qtyIdx = labels.findIndex(
    (l) =>
      l.includes("anz") ||
      l.includes("qty") ||
      l.includes("meng") ||
      l.includes("aufwand"),
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
  const descIdx = labels.findIndex(
    (l, i) =>
      i !== effectiveTitleIdx && (l.includes("beschr") || l.includes("desc")),
  );

  if (descIdx >= 0) row.cells[descIdx] = item.description ?? "";
  if (qtyIdx >= 0) row.cells[qtyIdx] = "1";
  if (unitIdx >= 0) row.cells[unitIdx] = item.defaultUnit ?? "Stk";
  if (priceIdx >= 0) row.cells[priceIdx] = formatCurrency(item.unitPriceNet);

  invoiceData.taxRate = Math.round(item.defaultTaxRate * 100);

  row.linkedItemId = item.id;
  itemSuggestions.value[ri] = [];
}

// ─── New Item Modal ────────────────────────────────────────────────────────
const showNewItemModal = ref(false);
const isCreatingItem = ref(false);
const newItemError = ref<string | null>(null);
const newItemTargetRow = ref<number>(0);

const newItem = reactive({
  name: "",
  description: "",
  defaultUnit: "",
  unitPriceNet: 0,
  defaultTaxRatePercent: 19,
});

function openNewItemModal(ri: number) {
  newItemTargetRow.value = ri;
  // Pre-fill name from what's typed
  newItem.name = invoiceData.tableRows[ri]?.cells[0] ?? "";
  newItem.description = "";
  newItem.defaultUnit = "";
  newItem.unitPriceNet = 0;
  newItem.defaultTaxRatePercent = 19;
  newItemError.value = null;
  showNewItemModal.value = true;
}

async function createNewItem() {
  newItemError.value = null;
  if (!newItem.name.trim()) {
    newItemError.value = "Name ist erforderlich.";
    return;
  }
  if (newItem.unitPriceNet == null || newItem.unitPriceNet < 0) {
    newItemError.value = "Netto-Preis muss >= 0 sein.";
    return;
  }

  isCreatingItem.value = true;
  try {
    const res = await fetch(`${API_BASE}/items`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name: newItem.name.trim(),
        description: newItem.description.trim() || null,
        defaultUnit: newItem.defaultUnit.trim() || null,
        unitPriceNet: newItem.unitPriceNet,
        defaultTaxRate: (newItem.defaultTaxRatePercent ?? 0) / 100,
        active: true,
      }),
    });

    if (!res.ok) {
      const err = await res
        .json()
        .catch(() => ({ message: `HTTP ${res.status}` }));
      throw new Error(err.message ?? `HTTP ${res.status}`);
    }

    const created: ItemDTO = await res.json();
    // Invalidate cache so next search picks it up
    allItemsCache = null;
    // Auto-select into the target row
    selectItem(newItemTargetRow.value, created);
    showNewItemModal.value = false;
  } catch (e: any) {
    newItemError.value = `Fehler: ${e.message}`;
  } finally {
    isCreatingItem.value = false;
  }
}

function onDragStart(e: DragEvent, idx: number) {
  dragIndex.value = idx;
  if (e.dataTransfer) e.dataTransfer.effectAllowed = "move";
}
function onDragOver(_e: DragEvent, idx: number) {
  dragOverIndex.value = idx;
}
function onDrop(_e: DragEvent, toIdx: number) {
  const from = dragIndex.value;
  if (from !== null && from !== toIdx) {
    const arr = [...sections.value];
    const [moved] = arr.splice(from, 1);
    arr.splice(toIdx, 0, moved);
    sections.value = arr;
  }
  onDragEnd();
}
function onDragEnd() {
  dragIndex.value = null;
  dragOverIndex.value = null;
}

// ─── Section management ───────────────────────────────────────────────────────

function toggleSection(id: string) {
  activeSection.value = activeSection.value === id ? null : id;
}
function removeSection(id: string) {
  sections.value = sections.value.filter((s) => s.id !== id);
  if (activeSection.value === id) activeSection.value = null;
}
const availableSectionsToAdd = computed(() =>
  ALL_SECTION_TYPES.filter(
    (t) => !sections.value.some((s) => s.type === t.type),
  ),
);
function addSection(type: string) {
  const tmpl = ALL_SECTION_TYPES.find((t) => t.type === type);
  if (!tmpl) return;
  const s: SectionDef = { id: uid(), ...tmpl };
  sections.value.push(s);
  nextTick(() => {
    activeSection.value = s.id;
  });
}

// ─── Logo Upload ──────────────────────────────────────────────────────────────

function triggerLogoUpload() {
  logoInputRef.value?.click();
}

function readLogoFile(file: File) {
  if (!file.type.startsWith("image/")) return;
  invoiceData.logoName = file.name;
  const reader = new FileReader();
  reader.onload = (ev) => {
    invoiceData.logo = ev.target?.result as string;
  };
  reader.readAsDataURL(file);
}
function onLogoFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0];
  if (file) readLogoFile(file);
}
function onLogoDrop(e: DragEvent) {
  logoDragOver.value = false;
  const file = e.dataTransfer?.files?.[0];
  if (file) readLogoFile(file);
}

// ─── Table Builder ────────────────────────────────────────────────────────────

function addColumn() {
  invoiceData.tableColumns.push({ label: "Neu", align: "left" });
  invoiceData.tableRows.forEach((r) => r.cells.push(""));
}
function removeColumn(ci: number) {
  invoiceData.tableColumns.splice(ci, 1);
  invoiceData.tableRows.forEach((r) => r.cells.splice(ci, 1));
}
function addRow() {
  invoiceData.tableRows.push({
    cells: invoiceData.tableColumns.map(() => ""),
    linkedItemId: null,
  });
}
function removeRow(ri: number) {
  invoiceData.tableRows.splice(ri, 1);
}

// ─── Computed Totals ──────────────────────────────────────────────────────────

const subtotal = computed(() =>
  invoiceData.tableRows.reduce((sum, row) => {
    const raw = (row.cells[row.cells.length - 1] ?? "")
      .replace(/[^\d,.-]/g, "")
      .replace(",", ".");
    return sum + (parseFloat(raw) || 0);
  }, 0),
);
const discountAmount = computed(
  () => subtotal.value * (invoiceData.discountRate / 100),
);
const afterDiscount = computed(() => subtotal.value - discountAmount.value);
const taxAmount = computed(
  () => afterDiscount.value * (invoiceData.taxRate / 100),
);
const grandTotal = computed(
  () => afterDiscount.value + (invoiceData.showTax ? taxAmount.value : 0),
);

// ─── Helpers ──────────────────────────────────────────────────────────────────

function formatDate(d: string): string {
  if (!d) return "";
  const [y, m, day] = d.split("-");
  return `${day}.${m}.${y}`;
}
function formatCurrency(n: number): string {
  return (
    n.toLocaleString("de-DE", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + "€"
  );
}

// ─── PDF Download ─────────────────────────────────────────────────────────────

async function downloadPDF() {
  await nextTick();

  const el = document.getElementById("invoice-preview");
  if (!el) return;

  // Collect all styles from the current page
  const styles = Array.from(document.styleSheets)
    .map((sheet) => {
      try {
        return Array.from(sheet.cssRules)
          .map((r) => r.cssText)
          .join("\n");
      } catch {
        return "";
      }
    })
    .join("\n");

  const win = window.open("", "_blank", "width=900,height=1200");
  if (!win) return;

  win.document.write(`
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="utf-8" />
        <title></title>
        <style>
          ${styles}
          body { margin: 0; background: white; }
          @page { margin: 15mm;  size: A4; }
        </style>
      </head>
      <body>${el.outerHTML}</body>
    </html>
  `);

  win.document.close();
  win.focus();

  // Wait for fonts/images to load before printing
  win.onload = () => {
    win.print();
    win.close();
  };

  // Fallback if onload already fired
  setTimeout(() => {
    if (!win.closed) {
      win.print();
      win.close();
    }
  }, 500);
}
</script>

<style scoped>
/* ── Customer search dropdown ─────────────────────────────────────────────── */
.customer-search-wrap {
  position: relative;
}

.customer-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  z-index: 999;
  overflow: hidden;
  margin-top: 3px;
}

.customer-suggestion-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
  width: 100%;
  padding: 9px 13px;
  background: none;
  border: none;
  border-bottom: 1px solid #f0f0ec;
  cursor: pointer;
  font-family: inherit;
  text-align: left;
  transition: background 0.1s;
}
.customer-suggestion-item:last-child {
  border-bottom: none;
}
.customer-suggestion-item:hover {
  background: #f4f4f2;
}

.suggestion-name {
  font-size: 13px;
  font-weight: 600;
  color: #111;
}
.suggestion-meta {
  font-size: 11px;
  color: #999;
}

.search-hint {
  font-size: 11px;
  color: #aaa;
  margin-top: 4px;
  display: block;
}
.search-hint--ok {
  color: #16a34a;
}

/* ── Save button ──────────────────────────────────────────────────────────── */
.btn-save {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  background: #fff;
  color: #1a56db;
  border: 2px solid #1a56db;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  pointer-events: all;
  transition:
    background 0.14s,
    color 0.14s;
  letter-spacing: -0.1px;
  white-space: nowrap;
}
.btn-save:hover {
  background: #ebf0fd;
}
.btn-save:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

/* ─── Root layout ────────────────────────────────────────────────────────── */
.creator-layout {
  display: flex;
  height: calc(100vh - 64px); /* subtract your navbar height */
  overflow: hidden;
  background: #f4f4f2;
  font-family: "Aspekta", "Helvetica Neue", sans-serif;
  position: relative;
}

/* ═══════════════════════════════════════════════════════
   EDITOR PANEL  (left 50%)
══════════════════════════════════════════════════════ */
.editor-panel {
  width: 50%;
  min-width: 300px;
  background: #fff;
  border-right: 1px solid #e8e8e4;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding: 24px 20px 14px;
  border-bottom: 1px solid #f0f0ec;
  flex-shrink: 0;
}

.editor-title {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: #111;
  margin: 0;
}

.section-count {
  font-size: 12px;
  color: #bbb;
}

.sections-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 5px;
  scrollbar-width: thin;
  scrollbar-color: #ddd transparent;
}
.sections-list::-webkit-scrollbar {
  width: 4px;
}
.sections-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

/* ── Section card ─────────────────────────────────────────────────────────── */
.section-card {
  background: #fafaf8;
  border: 1px solid #e8e8e4;
  border-radius: 10px;
  overflow: hidden;
  transition:
    border-color 0.14s,
    box-shadow 0.14s,
    opacity 0.14s;
}
.section-card:hover {
  border-color: #ccc;
}
.section-card--active {
  border-color: #111;
  box-shadow: 0 0 0 1px #111;
}
.section-card--dragging {
  opacity: 0.45;
}
.section-card--dragover {
  border-color: #1a56db;
  box-shadow: 0 0 0 2px rgba(26, 86, 219, 0.22);
}

.section-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 11px 13px;
  cursor: pointer;
  user-select: none;
}

.section-card__left {
  display: flex;
  align-items: center;
  gap: 9px;
}
.section-card__right {
  display: flex;
  align-items: center;
  gap: 6px;
}

.drag-handle {
  color: #ccc;
  font-size: 15px;
  cursor: grab;
  line-height: 1;
}
.drag-handle:active {
  cursor: grabbing;
}

.section-name {
  font-size: 13px;
  font-weight: 600;
  color: #1a1a1a;
  letter-spacing: -0.1px;
}

.chevron {
  font-size: 17px;
  color: #aaa;
  display: inline-block;
  transform: rotate(0deg);
  transition: transform 0.2s ease;
  line-height: 1;
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
  color: #ccc;
  transition:
    color 0.12s,
    background 0.12s;
  line-height: 1.4;
}
.btn-icon--danger:hover {
  color: #e53e3e;
  background: #fff5f5;
}

.section-card__body {
  padding: 4px 13px 16px;
  border-top: 1px solid #f0f0ec;
}

/* ── Slide transition ─────────────────────────────────────────────────────── */
.slide-enter-active,
.slide-leave-active {
  transition:
    max-height 0.22s ease,
    opacity 0.18s ease;
  overflow: hidden;
}
.slide-enter-from,
.slide-leave-to {
  max-height: 0;
  opacity: 0;
}
.slide-enter-to,
.slide-leave-from {
  max-height: 2400px;
  opacity: 1;
}

/* ── Form fields ──────────────────────────────────────────────────────────── */
.field-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 10px;
}

.field-group > label {
  font-size: 11px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.4px;
}

.field-row {
  display: flex;
  gap: 10px;
}
.field-row .field-group {
  flex: 1;
  min-width: 0;
}

input[type="text"],
input[type="date"],
input[type="number"],
textarea,
select {
  border: 1px solid #e2e2de;
  border-radius: 6px;
  padding: 7px 10px;
  font-size: 13px;
  font-family: inherit;
  color: #1a1a1a;
  background: #fff;
  width: 100%;
  box-sizing: border-box;
  outline: none;
  transition:
    border-color 0.14s,
    box-shadow 0.14s;
}
input:focus,
textarea:focus,
select:focus {
  border-color: #111;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.055);
}
textarea {
  resize: vertical;
  min-height: 64px;
}

.btn-add-entry {
  margin-top: 9px;
  padding: 6px 12px;
  font-size: 12px;
  font-family: inherit;
  color: #888;
  background: none;
  border: 1px dashed #d8d8d4;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.14s;
}
.btn-add-entry:hover {
  border-color: #111;
  color: #111;
}

/* ── Logo upload ──────────────────────────────────────────────────────────── */
.logo-upload {
  border: 1.5px dashed #ddd;
  border-radius: 8px;
  padding: 13px 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 12.5px;
  color: #888;
  min-height: 50px;
  transition:
    border-color 0.14s,
    background 0.14s;
}
.logo-upload:hover,
.logo-upload--dragover {
  border-color: #111;
  background: #f9f9f7;
}
.logo-upload--has-file {
  border-style: solid;
  border-color: #e0e0dc;
  cursor: default;
}
.logo-upload__arrow {
  font-size: 18px;
  flex-shrink: 0;
}
.logo-upload__label {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 3px;
}
.logo-upload__hint {
  color: #bbb;
}
.logo-upload__btn {
  background: #f0f0ec;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 3px 10px;
  font-size: 12px;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.12s;
  color: #333;
}
.logo-upload__btn:hover {
  background: #e4e4e0;
}

.logo-preview {
  max-height: 40px;
  max-width: 180px;
  object-fit: contain;
  display: block;
}

.logo-remove {
  margin-top: 5px;
  font-size: 11px;
  color: #e53e3e;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  font-family: inherit;
}
.logo-remove:hover {
  opacity: 0.72;
}

/* ── Table builder ────────────────────────────────────────────────────────── */
.table-section-group {
  margin-top: 14px;
}

.table-section-label {
  display: block;
  font-size: 11px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  margin-bottom: 7px;
}

.column-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-bottom: 7px;
}

.column-row {
  display: flex;
  align-items: center;
  gap: 5px;
  background: #f4f4f2;
  border: 1px solid #e4e4e0;
  border-radius: 7px;
  padding: 4px 8px;
}

.column-name-input {
  flex: 1;
  border: none !important;
  background: transparent;
  padding: 4px 4px;
  font-size: 12.5px;
  box-shadow: none !important;
  width: auto;
}
.column-name-input:focus {
  border: none;
  box-shadow: none;
  outline: none;
}

.column-align-select {
  width: auto;
  border: none !important;
  background: transparent;
  font-size: 11px;
  color: #888;
  cursor: pointer;
  padding: 2px 3px;
  box-shadow: none !important;
}
.column-align-select:focus {
  box-shadow: none;
  border: none;
}

.btn-add-col {
  font-size: 12px;
  color: #666;
  background: none;
  border: 1.5px dashed #d8d8d4;
  border-radius: 6px;
  padding: 5px 12px;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.14s;
  display: inline-flex;
  align-items: center;
}
.btn-add-col:hover {
  border-color: #111;
  color: #111;
}

.row-editor {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0ec;
}
.row-num {
  font-size: 10.5px;
  color: #ccc;
  min-width: 17px;
  margin-top: 8px;
  text-align: center;
  flex-shrink: 0;
}
.row-cells {
  display: flex;
  flex: 1;
  gap: 4px;
  flex-wrap: wrap;
}
.row-cell-input {
  flex: 1;
  min-width: 68px;
  font-size: 12px;
  padding: 5px 7px;
}

.btn-add-row {
  margin-top: 7px;
  width: 100%;
  padding: 8px;
  background: none;
  border: 1.5px dashed #d8d8d4;
  border-radius: 6px;
  font-size: 12px;
  font-family: inherit;
  color: #888;
  cursor: pointer;
  transition: all 0.14s;
}
.btn-add-row:hover {
  border-color: #111;
  color: #111;
}

.toggle-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 20px;
  margin-top: 10px;
}
.toggle-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #555;
  cursor: pointer;
  font-weight: 400;
  text-transform: none;
  letter-spacing: 0;
}
.toggle-label input[type="checkbox"] {
  width: auto;
  border: none;
  padding: 0;
  box-shadow: none;
  cursor: pointer;
  accent-color: #111;
}

/* ── Add section footer ───────────────────────────────────────────────────── */
.add-section-area {
  padding: 10px 12px 18px;
  border-top: 1px solid #f0f0ec;
  flex-shrink: 0;
}
.add-section-label {
  font-size: 11px;
  font-weight: 600;
  color: #bbb;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  margin: 0 0 7px;
}
.add-section-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
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
  font-family: inherit;
  color: #555;
  cursor: pointer;
  transition: all 0.14s;
}
.btn-add-section:hover {
  background: #111;
  color: #fff;
  border-color: #111;
}

/* ═══════════════════════════════════════════════════════
   PREVIEW PANEL  (right 50%)
══════════════════════════════════════════════════════ */
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
  padding: 24px 24px 14px;
  flex-shrink: 0;
}

.preview-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.template-select {
  width: auto;
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 7px;
  cursor: pointer;
  font-family: inherit;
}

.preview-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 0 24px 96px;
  display: flex;
  justify-content: center;
  scrollbar-width: thin;
  scrollbar-color: #ccc transparent;
}
.preview-wrapper::-webkit-scrollbar {
  width: 4px;
}
.preview-wrapper::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 2px;
}

.preview-scale-container {
  width: 100%;
  max-width: 680px;
}

/* ── Download bar ─────────────────────────────────────────────────────────── */
.download-bar {
  position: absolute;
  bottom: 10%;
  right: 0;
  width: 50%;
  padding: 14px 24px;
  display: flex;
  justify-content: stretch;
  gap: 10px;
  background: linear-gradient(to top, #f4f4f2 70%, transparent);
  pointer-events: none;
}

.btn-download {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  background: #1a56db;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  pointer-events: all;
  box-shadow: 0 2px 14px rgba(26, 86, 219, 0.32);
  transition:
    background 0.14s,
    transform 0.1s,
    box-shadow 0.14s;
  letter-spacing: -0.1px;
}
.btn-download:hover {
  background: #1447b8;
  box-shadow: 0 4px 18px rgba(26, 86, 219, 0.42);
}
.btn-download:active {
  transform: scale(0.97);
}

/* ═══════════════════════════════════════════════════════
   INVOICE PREVIEW — A4 sheet
══════════════════════════════════════════════════════ */
.invoice-preview {
  background: #fff;
  border-radius: 3px;
  box-shadow: 0 2px 24px rgba(0, 0, 0, 0.09);
  padding: 52px 56px 64px;
  font-family: "Aspekta", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 11px;
  color: #222;
  line-height: 1.55;
  min-height: 900px;
}

/* Header */
.inv-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}
.inv-logo-placeholder {
  display: flex;
  align-items: center;
  gap: 8px;
}
.inv-logo-dot {
  width: 18px;
  height: 18px;
  background: #1a56db;
  border-radius: 50%;
  flex-shrink: 0;
}
.inv-logo-wordmark {
  font-size: 14px;
  font-weight: 700;
  color: #111;
  letter-spacing: -0.3px;
}
.inv-logo-img {
  max-height: 44px;
  max-width: 140px;
  object-fit: contain;
  display: block;
}

.inv-header__company {
  display: flex;
  flex-direction: column;
  text-align: right;
  font-size: 10.5px;
  color: #666;
  line-height: 1.65;
  gap: 1px;
}
.inv-header__company strong {
  color: #111;
  font-weight: 700;
  font-size: 11px;
}

/* Customer */
.inv-customer {
  font-size: 10.5px;
  color: #444;
  line-height: 1.7;
  margin-bottom: 20px;
}
.inv-customer p {
  margin: 0;
}

/* Meta */
.inv-meta {
  margin-bottom: 26px;
}
.inv-meta__grid {
  display: grid;
  grid-template-columns: auto 1fr;
  column-gap: 14px;
  row-gap: 1px;
  font-size: 10.5px;
  color: #444;
}
.inv-meta__key {
  color: #aaa;
  font-size: 10px;
  white-space: nowrap;
}

/* Greeting */
.inv-greeting {
  margin-bottom: 22px;
  font-size: 10.5px;
  color: #444;
}
.inv-greeting__title {
  font-size: 20px;
  font-weight: 700;
  color: #111;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}
.inv-greeting__body {
  margin: 0;
  line-height: 1.6;
}

/* ── Invoice table ────────────────────────────────────────────────────────── */
.inv-table-section {
  margin-bottom: 20px;
}

.inv-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 10px;
}

/* CLASSIC (default) */
.inv-table thead tr {
  background: #f0f0ec;
}

.inv-table thead th {
  padding: 8px 9px;
  font-weight: 700;
  font-size: 9px;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  color: #666;
  border: none;
}

.inv-table tbody tr {
  border-bottom: 1px solid #f4f4f2;
}
.inv-tr--alt {
  background: #fafaf8;
}

.inv-table tbody td {
  padding: 8px 9px;
  color: #333;
  vertical-align: top;
}

/* Totals */
.inv-totals {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-top: 4px;
  gap: 1px;
}

.inv-totals__row {
  display: flex;
  justify-content: space-between;
  gap: 40px;
  padding: 5px 9px;
  font-size: 10px;
  color: #666;
  width: 280px;
}

.inv-totals__row--discount {
  color: #e53e3e;
}
.inv-totals__row--tax {
  color: #555;
}

.inv-totals__row--grand {
  background: #f0f0ec;
  font-weight: 700;
  font-size: 11.5px;
  color: #111;
  margin-top: 3px;
}

/* Banking */
.inv-banking {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #e8e8e4;
  font-size: 10px;
  color: #555;
  line-height: 1.8;
}
.inv-banking__col p {
  margin: 0;
}
.inv-banking__col--right {
  text-align: right;
}

/* Footer */
.inv-footer {
  margin-top: 22px;
  font-size: 10px;
  color: #666;
}
.inv-footer__payment {
  margin-bottom: 8px;
}
.inv-footer__closing {
  text-align: center;
  margin: 20px 0;
}
.inv-footer__bar {
  border-top: 1px solid #e8e8e4;
  padding-top: 10px;
  text-align: center;
  color: #aaa;
  font-size: 9px;
  margin-top: 20px;
}

/* ═══════════════════════════════════════════════════════
   TEMPLATE VARIANTS
══════════════════════════════════════════════════════ */

/* Modern */
.template--modern .inv-table thead tr {
  background: #111;
}
.template--modern .inv-table thead th {
  color: #e0e0dc;
}
.template--modern .inv-tr--alt {
  background: #f9f9f7;
}
.template--modern .inv-totals__row--grand {
  background: #111;
  color: #fff;
}
.template--modern .inv-logo-dot {
  border-radius: 3px;
  background: #111;
}

/* Minimal */
.template--minimal .inv-header {
  border-bottom: 2px solid #111;
  padding-bottom: 16px;
}
.template--minimal .inv-table thead tr {
  background: transparent;
}
.template--minimal .inv-table thead th {
  color: #111;
  border-bottom: 2px solid #111;
}
.template--minimal .inv-tr--alt {
  background: transparent;
}
.template--minimal .inv-table tbody tr {
  border-bottom: 1px solid #e4e4e0;
}
.template--minimal .inv-totals__row--grand {
  background: transparent;
  border-top: 2px solid #111;
}
.template--minimal .inv-logo-dot {
  background: #111;
}

/* ─── Item search dropdown (per row) ─────────────────────────────────────── */
.row-cell-search-wrap {
  position: relative;
  flex: 1;
  min-width: 100px;
}

.item-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 240px;
  background: #fff;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  z-index: 999;
  overflow: hidden;
  margin-top: 2px;
}

.item-suggestion-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
  width: 100%;
  padding: 8px 12px;
  background: none;
  border: none;
  border-bottom: 1px solid #f0f0ec;
  cursor: pointer;
  font-family: inherit;
  text-align: left;
  transition: background 0.1s;
}
.item-suggestion-item:last-child {
  border-bottom: none;
}
.item-suggestion-item:hover {
  background: #f4f4f2;
}

/* ─── New Customer Button ────────────────────────────────────────────────── */
.customer-not-found {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 5px;
}

.not-found-text {
  font-size: 12px;
  color: #aaa;
}

.btn-create-inline {
  font-size: 12px;
  font-family: inherit;
  font-weight: 600;
  color: #1a56db;
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 2px;
  transition: color 0.12s;
}
.btn-create-inline:hover {
  color: #1447b8;
}

/* ─── Modal ──────────────────────────────────────────────────────────────── */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.38);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.modal-box {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 8px 48px rgba(0, 0, 0, 0.18);
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 14px;
  border-bottom: 1px solid #f0f0ec;
  flex-shrink: 0;
}

.modal-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #111;
  letter-spacing: -0.3px;
}

.modal-close {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  color: #aaa;
  padding: 4px 8px;
  border-radius: 6px;
  transition:
    color 0.12s,
    background 0.12s;
}
.modal-close:hover {
  color: #111;
  background: #f4f4f2;
}

.modal-body {
  padding: 16px 24px;
  flex: 1;
}

.modal-body .field-group {
  margin-top: 10px;
}
.modal-body .field-row {
  display: flex;
  gap: 10px;
}
.modal-body .field-row .field-group {
  flex: 1;
  min-width: 0;
}
.modal-body label {
  font-size: 11px;
  font-weight: 600;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  display: block;
  margin-bottom: 4px;
}
.modal-body input,
.modal-body select {
  border: 1px solid #e2e2de;
  border-radius: 6px;
  padding: 7px 10px;
  font-size: 13px;
  font-family: inherit;
  color: #1a1a1a;
  background: #fff;
  width: 100%;
  box-sizing: border-box;
  outline: none;
  transition:
    border-color 0.14s,
    box-shadow 0.14s;
}
.modal-body input:focus,
.modal-body select:focus {
  border-color: #111;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.055);
}

.modal-error {
  margin-top: 12px;
  padding: 8px 12px;
  background: #fff5f5;
  border: 1px solid #fed7d7;
  border-radius: 6px;
  font-size: 12px;
  color: #e53e3e;
}

.modal-footer {
  padding: 14px 24px 20px;
  border-top: 1px solid #f0f0ec;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  flex-shrink: 0;
}

.btn-modal-cancel {
  padding: 9px 18px;
  background: none;
  border: 1px solid #e2e2de;
  border-radius: 7px;
  font-size: 13px;
  font-family: inherit;
  color: #555;
  cursor: pointer;
  transition: background 0.12s;
}
.btn-modal-cancel:hover {
  background: #f4f4f2;
}

.btn-modal-save {
  padding: 9px 20px;
  background: #1a56db;
  border: none;
  border-radius: 7px;
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  color: #fff;
  cursor: pointer;
  transition: background 0.12s;
}
.btn-modal-save:hover {
  background: #1447b8;
}
.btn-modal-save:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

/* ═══════════════════════════════════════════════════════
   RESPONSIVE
══════════════════════════════════════════════════════ */
@media (max-width: 1024px) {
  .creator-layout {
    flex-direction: column;
    height: auto;
    overflow: auto;
  }
  .editor-panel {
    width: 100%;
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
    padding: 0 16px 80px;
  }
  .download-bar {
    width: 100%;
    position: sticky;
    bottom: 0;
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
    gap: 10px;
  }
  .toggle-grid {
    flex-direction: column;
    gap: 8px;
  }
}

/* ═══════════════════════════════════════════════════════
   PRINT
══════════════════════════════════════════════════════ */
@media print {
  .editor-panel,
  .preview-header,
  .download-bar {
    display: none !important;
  }
  .creator-layout {
    display: block;
  }
  .preview-panel {
    background: white;
  }
  .preview-wrapper {
    overflow: visible;
    padding: 0;
  }
  .preview-scale-container {
    max-width: 100%;
  }
  .invoice-preview {
    box-shadow: none;
    padding: 0;
    min-height: 0;
  }
}
</style>
