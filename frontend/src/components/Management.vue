<template>
  <div class="mgmt">
    <!-- ══════════════ TAB BAR ══════════════ -->
    <div class="tab-bar">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        class="tab"
        :class="{ 'tab--active': activeTab === tab.id }"
        @click="activeTab = tab.id"
      >
        <span class="tab__icon" v-html="tab.icon"></span>
        {{ tab.label }}
        <span class="tab__count">{{ tabCount(tab.id) }}</span>
      </button>
    </div>

    <!-- ══════════════ RECHNUNGEN ══════════════ -->
    <section v-if="activeTab === 'invoices'">
      <div class="toolbar">
        <div class="search-box">
          <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
            <circle
              cx="6.5"
              cy="6.5"
              r="4.5"
              stroke="#999"
              stroke-width="1.4"
            />
            <path
              d="M10 10l3.5 3.5"
              stroke="#999"
              stroke-width="1.4"
              stroke-linecap="round"
            />
          </svg>
          <input v-model="invSearch" placeholder="Suchen…" />
        </div>
        <div class="chips">
          <button
            v-for="s in invStatuses"
            :key="s.val"
            class="chip"
            :class="{ 'chip--on': invFilter === s.val }"
            @click="invFilter = s.val"
          >
            {{ s.label }}
          </button>
        </div>
      </div>

      <div class="card">
        <div v-if="invLoading" class="loader"><div class="spin"></div></div>
        <div v-else-if="filteredInv.length === 0" class="empty">
          Keine Rechnungen gefunden
        </div>
        <table v-else class="tbl">
          <thead>
            <tr>
              <th>Nummer</th>
              <th>Kunde</th>
              <th>Datum</th>
              <th class="r">Netto</th>
              <th class="r">Brutto</th>
              <th>Status</th>
              <th class="r">Aktionen</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="inv in filteredInv" :key="inv.id">
              <td class="mono">{{ inv.invoiceNumber }}</td>
              <td>
                <div class="name-cell">
                  <div class="avatar">
                    {{
                      initials(inv.customer?.forename, inv.customer?.surname)
                    }}
                  </div>
                  <span
                    >{{ inv.customer?.forename }}
                    {{ inv.customer?.surname }}</span
                  >
                </div>
              </td>
              <td class="muted">{{ fmtDate(inv.issueDate) }}</td>
              <td class="r mono">{{ fmtMoney(inv.netTotal) }}</td>
              <td class="r mono bold">{{ fmtMoney(inv.grossTotal) }}</td>
              <td>
                <span
                  class="badge"
                  :class="`b-${(inv.status || '').toLowerCase()}`"
                  >{{ statusLabel(inv.status) }}</span
                >
              </td>
              <td class="r">
                <div class="acts">
                  <button class="act" @click="openInvDetail(inv)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
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
                  <button class="act" @click="openEditInv(inv)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path
                        d="M10.5 3l2.5 2.5L5 13.5H2.5V11L10.5 3z"
                        stroke="currentColor"
                        stroke-width="1.5"
                        stroke-linejoin="round"
                      />
                    </svg>
                    Bearbeiten
                  </button>
                  <button class="act" @click="duplicateInv(inv)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
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
                  <button class="act" @click="printInv(inv)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
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
                  <button
                    class="act act--del"
                    @click="
                      askDelete('Rechnung', inv.invoiceNumber, () =>
                        deleteInv(inv.id),
                      )
                    "
                  >
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
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

    <!-- ══════════════ KUNDEN ══════════════ -->
    <section v-if="activeTab === 'customers'">
      <div class="toolbar">
        <div class="search-box">
          <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
            <circle
              cx="6.5"
              cy="6.5"
              r="4.5"
              stroke="#999"
              stroke-width="1.4"
            />
            <path
              d="M10 10l3.5 3.5"
              stroke="#999"
              stroke-width="1.4"
              stroke-linecap="round"
            />
          </svg>
          <input v-model="custSearch" placeholder="Name, E-Mail, Stadt…" />
        </div>
        <button class="btn-add" @click="openCustomerForm()">
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

      <div class="card">
        <div v-if="custLoading" class="loader"><div class="spin"></div></div>
        <div v-else-if="filteredCust.length === 0" class="empty">
          Keine Kunden gefunden
        </div>
        <table v-else class="tbl">
          <thead>
            <tr>
              <th>Name</th>
              <th>Adresse</th>
              <th>Typ</th>
              <th>E-Mail</th>
              <th>Telefon</th>
              <th>USt-IdNr.</th>
              <th class="r">Aktionen</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in filteredCust" :key="c.id">
              <td>
                <div class="name-cell">
                  <div class="avatar">
                    {{ initials(c.forename, c.surname) }}
                  </div>
                  <div>
                    <div class="bold">{{ c.forename }} {{ c.surname }}</div>
                  </div>
                </div>
              </td>
              <td>{{ c.street }}, {{ c.zip }} {{ c.city }}</td>
              <td>
                <span
                  class="badge"
                  :class="c.type === 'BUSINESS' ? 'b-sent' : 'b-draft'"
                  >{{ c.type === "BUSINESS" ? "Unternehmen" : "Privat" }}</span
                >
              </td>
              <td class="muted">{{ c.email || "—" }}</td>
              <td class="muted">{{ c.phone || "—" }}</td>
              <td class="mono">{{ c.vatId || "—" }}</td>
              <td class="r">
                <div class="acts">
                  <button class="act" @click="openCustomerForm(c)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path
                        d="M10.5 3l2.5 2.5L5 13.5H2.5V11L10.5 3z"
                        stroke="currentColor"
                        stroke-width="1.5"
                        stroke-linejoin="round"
                      />
                    </svg>
                    Bearbeiten
                  </button>
                  <button
                    class="act act--del"
                    @click="
                      askDelete('Kunden', `${c.forename} ${c.surname}`, () =>
                        deleteCust(c.id),
                      )
                    "
                  >
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
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

    <!-- ══════════════ ARTIKEL ══════════════ -->
    <section v-if="activeTab === 'items'">
      <div class="toolbar">
        <div class="search-box">
          <svg width="15" height="15" viewBox="0 0 15 15" fill="none">
            <circle
              cx="6.5"
              cy="6.5"
              r="4.5"
              stroke="#999"
              stroke-width="1.4"
            />
            <path
              d="M10 10l3.5 3.5"
              stroke="#999"
              stroke-width="1.4"
              stroke-linecap="round"
            />
          </svg>
          <input v-model="itemSearch" placeholder="Artikel suchen…" />
        </div>
        <div class="chips">
          <button
            class="chip"
            :class="{ 'chip--on': itemFilter === 'all' }"
            @click="itemFilter = 'all'"
          >
            Alle
          </button>
          <button
            class="chip"
            :class="{ 'chip--on': itemFilter === 'active' }"
            @click="itemFilter = 'active'"
          >
            Aktiv
          </button>
          <button
            class="chip"
            :class="{ 'chip--on': itemFilter === 'inactive' }"
            @click="itemFilter = 'inactive'"
          >
            Inaktiv
          </button>
        </div>
        <button class="btn-add" @click="openItemForm()">
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

      <div class="card">
        <div v-if="itemsLoading" class="loader"><div class="spin"></div></div>
        <div v-else-if="filteredItems.length === 0" class="empty">
          Keine Artikel gefunden
        </div>
        <table v-else class="tbl">
          <thead>
            <tr>
              <th>Name</th>
              <th>Beschreibung</th>
              <th>Einheit</th>
              <th class="r">Netto-Preis</th>
              <th class="r">MwSt.</th>
              <th>Status</th>
              <th class="r">Aktionen</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredItems" :key="item.id">
              <td class="bold">{{ item.name }}</td>
              <td class="muted">{{ item.description || "—" }}</td>
              <td class="mono">{{ item.defaultUnit || "pcs" }}</td>
              <td class="r mono">{{ fmtMoney(item.unitPriceNet) }}</td>
              <td class="r mono">
                {{ (item.defaultTaxRate * 100).toFixed(0) }}%
              </td>
              <td>
                <span
                  class="badge"
                  :class="item.active ? 'b-paid' : 'b-cancelled'"
                  >{{ item.active ? "Aktiv" : "Inaktiv" }}</span
                >
              </td>
              <td class="r">
                <div class="acts">
                  <button class="act" @click="openItemForm(item)">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                      <path
                        d="M10.5 3l2.5 2.5L5 13.5H2.5V11L10.5 3z"
                        stroke="currentColor"
                        stroke-width="1.5"
                        stroke-linejoin="round"
                      />
                    </svg>
                    Bearbeiten
                  </button>
                  <button class="act" @click="toggleItem(item)">
                    <svg
                      v-if="item.active"
                      width="16"
                      height="16"
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
                      width="16"
                      height="16"
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

    <!-- ══════════════ RECHNUNG DETAIL MODAL ══════════════ -->
    <Teleport to="body">
      <div v-if="detailInv" class="overlay" @click.self="detailInv = null">
        <div class="modal modal--lg">
          <div class="modal-head">
            <div class="modal-head__left">
              <span class="modal-title">{{ detailInv.invoiceNumber }}</span>
              <span
                class="badge"
                :class="`b-${(detailInv.status || '').toLowerCase()}`"
                >{{ statusLabel(detailInv.status) }}</span
              >
            </div>
            <div class="modal-head__right">
              <button class="btn-sm" @click="printInv(detailInv)">
                Drucken
              </button>
              <button
                class="btn-sm"
                @click="
                  openEditInv(detailInv);
                  detailInv = null;
                "
              >
                Bearbeiten
              </button>
              <button class="modal-x" @click="detailInv = null">✕</button>
            </div>
          </div>
          <div class="modal-body">
            <div class="detail-grid">
              <div>
                <p class="detail-label">Kunde</p>
                <p class="detail-val bold">
                  {{ detailInv.customer?.forename }}
                  {{ detailInv.customer?.surname }}
                </p>
                <p class="detail-val muted">{{ detailInv.customer?.street }}</p>
                <p class="detail-val muted">
                  {{ detailInv.customer?.zip }} {{ detailInv.customer?.city }}
                </p>
              </div>
              <div>
                <p class="detail-label">Rechnungsinfo</p>
                <div class="kv">
                  <span class="muted">Nummer</span
                  ><span>{{ detailInv.invoiceNumber }}</span>
                </div>
                <div class="kv">
                  <span class="muted">Datum</span
                  ><span>{{ fmtDate(detailInv.issueDate) }}</span>
                </div>
                <div class="kv">
                  <span class="muted">Leistungsdatum</span
                  ><span>{{ fmtDate(detailInv.serviceDate) }}</span>
                </div>
                <div class="kv">
                  <span class="muted">Währung</span
                  ><span>{{ detailInv.currency }}</span>
                </div>
              </div>
            </div>
            <table class="tbl" style="margin-top: 20px">
              <thead>
                <tr>
                  <th>Pos.</th>
                  <th>Bezeichnung</th>
                  <th class="r">Menge</th>
                  <th>Einheit</th>
                  <th class="r">Netto/Einh.</th>
                  <th class="r">MwSt.</th>
                  <th class="r">Netto</th>
                  <th class="r">Brutto</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="it in detailInv.items" :key="it.id">
                  <td class="muted">{{ it.position }}</td>
                  <td>{{ it.title }}</td>
                  <td class="r mono">{{ it.quantity }}</td>
                  <td class="mono">{{ it.unit }}</td>
                  <td class="r mono">{{ fmtMoney(it.unitPriceNet) }}</td>
                  <td class="r muted">{{ (it.taxRate * 100).toFixed(0) }}%</td>
                  <td class="r mono">{{ fmtMoney(it.netTotal) }}</td>
                  <td class="r mono bold">{{ fmtMoney(it.grossTotal) }}</td>
                </tr>
              </tbody>
            </table>
            <div class="totals-block">
              <div class="total-row">
                <span class="muted">Netto</span
                ><span>{{ fmtMoney(detailInv.netTotal) }}</span>
              </div>
              <div class="total-row">
                <span class="muted">MwSt.</span
                ><span>{{ fmtMoney(detailInv.taxTotal) }}</span>
              </div>
              <div class="total-row total-row--grand">
                <span>Gesamt (Brutto)</span
                ><span>{{ fmtMoney(detailInv.grossTotal) }}</span>
              </div>
            </div>
            <p v-if="detailInv.notes" class="notes-block">
              {{ detailInv.notes }}
            </p>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ══════════════ RECHNUNG BEARBEITEN MODAL ══════════════ -->
    <Teleport to="body">
      <div
        v-if="editInvModal"
        class="overlay"
        @click.self="editInvModal = false"
      >
        <div class="modal modal--lg">
          <div class="modal-head">
            <span class="modal-title">Rechnung bearbeiten</span>
            <button class="modal-x" @click="editInvModal = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-row">
              <div class="fg">
                <label>Status</label>
                <select v-model="invForm.status">
                  <option value="DRAFT">Entwurf</option>
                  <option value="SENT">Versendet</option>
                  <option value="PAID">Bezahlt</option>
                  <option value="CANCELLED">Storniert</option>
                </select>
              </div>
              <div class="fg">
                <label>Rechnungsdatum</label
                ><input type="date" v-model="invForm.issueDate" />
              </div>
              <div class="fg">
                <label>Leistungsdatum</label
                ><input type="date" v-model="invForm.serviceDate" />
              </div>
            </div>
            <div class="fg">
              <label>Notizen</label
              ><textarea v-model="invForm.notes" rows="3"></textarea>
            </div>

            <div style="margin-top: 20px">
              <div class="section-label">Positionen</div>
              <div v-for="(it, i) in invForm.items" :key="i" class="item-row">
                <span class="item-pos">{{ i + 1 }}</span>
                <div class="item-fields">
                  <div class="item-fields-row">
                    <div class="fg" style="flex: 3">
                      <label>Bezeichnung *</label
                      ><input
                        v-model="it.title"
                        placeholder="z.B. Beratungsstunde"
                      />
                    </div>
                    <div class="fg" style="flex: 1">
                      <label>Menge</label
                      ><input
                        v-model.number="it.quantity"
                        type="number"
                        min="0.001"
                        step="0.001"
                      />
                    </div>
                    <div class="fg" style="flex: 1">
                      <label>Einheit</label
                      ><input v-model="it.unit" placeholder="h / Stk" />
                    </div>
                  </div>
                  <div class="item-fields-row">
                    <div class="fg" style="flex: 2">
                      <label>Netto/Einheit (€)</label
                      ><input
                        v-model.number="it.unitPriceNet"
                        type="number"
                        min="0"
                        step="0.01"
                      />
                    </div>
                    <div class="fg" style="flex: 1">
                      <label>MwSt. (%)</label
                      ><input
                        v-model.number="it.taxRatePct"
                        type="number"
                        min="0"
                        max="100"
                      />
                    </div>
                  </div>
                </div>
                <button
                  class="act act--del item-del"
                  @click="invForm.items.splice(i, 1)"
                  title="Entfernen"
                >
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
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
                </button>
              </div>
              <button
                class="btn-ghost"
                @click="
                  invForm.items.push({
                    title: '',
                    quantity: 1,
                    unit: 'pcs',
                    unitPriceNet: 0,
                    taxRatePct: 19,
                  })
                "
              >
                + Position hinzufügen
              </button>
            </div>
            <p v-if="invFormErr" class="err">{{ invFormErr }}</p>
          </div>
          <div class="modal-foot">
            <button class="btn-cancel" @click="editInvModal = false">
              Abbrechen
            </button>
            <button class="btn-save" @click="saveInv" :disabled="savingInv">
              {{ savingInv ? "Speichern…" : "Speichern" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ══════════════ KUNDEN MODAL ══════════════ -->
    <Teleport to="body">
      <div v-if="custModal" class="overlay" @click.self="custModal = false">
        <div class="modal">
          <div class="modal-head">
            <span class="modal-title">{{
              editCust?.id ? "Kunde bearbeiten" : "Neuer Kunde"
            }}</span>
            <button class="modal-x" @click="custModal = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="fg">
              <label>Typ</label>
              <select v-model="custForm.type">
                <option value="PRIVATE">Privatperson</option>
                <option value="BUSINESS">Unternehmen</option>
              </select>
            </div>
            <div class="form-row">
              <div class="fg">
                <label>Vorname *</label
                ><input v-model="custForm.forename" placeholder="Max" />
              </div>
              <div class="fg">
                <label>Nachname *</label
                ><input v-model="custForm.surname" placeholder="Mustermann" />
              </div>
            </div>
            <div class="fg">
              <label>Straße & Hausnummer *</label
              ><input v-model="custForm.street" placeholder="Musterstraße 1" />
            </div>
            <div class="form-row">
              <div class="fg">
                <label>PLZ *</label
                ><input v-model="custForm.zip" placeholder="12345" />
              </div>
              <div class="fg">
                <label>Stadt *</label
                ><input v-model="custForm.city" placeholder="Berlin" />
              </div>
              <div class="fg">
                <label>Land</label
                ><input
                  v-model="custForm.country"
                  maxlength="2"
                  placeholder="DE"
                />
              </div>
            </div>
            <div class="form-row">
              <div class="fg">
                <label>E-Mail *</label
                ><input
                  v-model="custForm.email"
                  type="email"
                  placeholder="max@beispiel.de"
                />
              </div>
              <div class="fg">
                <label>Telefon *</label
                ><input v-model="custForm.phone" placeholder="+49 30 123456" />
              </div>
            </div>
            <div class="fg">
              <label>USt-IdNr.</label
              ><input v-model="custForm.vatId" placeholder="DE123456789" />
            </div>
            <p v-if="custFormErr" class="err">{{ custFormErr }}</p>
          </div>
          <div class="modal-foot">
            <button class="btn-cancel" @click="custModal = false">
              Abbrechen
            </button>
            <button class="btn-save" @click="saveCust" :disabled="savingCust">
              {{
                savingCust
                  ? "Speichern…"
                  : editCust?.id
                    ? "Speichern"
                    : "Anlegen"
              }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ══════════════ ARTIKEL MODAL ══════════════ -->
    <Teleport to="body">
      <div v-if="itemModal" class="overlay" @click.self="itemModal = false">
        <div class="modal">
          <div class="modal-head">
            <span class="modal-title">{{
              editItem?.id ? "Artikel bearbeiten" : "Neuer Artikel"
            }}</span>
            <button class="modal-x" @click="itemModal = false">✕</button>
          </div>
          <div class="modal-body">
            <div class="fg">
              <label>Name *</label
              ><input
                v-model="itemForm.name"
                placeholder="z.B. Beratungsstunde"
              />
            </div>
            <div class="fg">
              <label>Beschreibung</label
              ><input
                v-model="itemForm.description"
                placeholder="Kurze Beschreibung (optional)"
              />
            </div>
            <div class="form-row">
              <div class="fg">
                <label>Netto-Preis *</label
                ><input
                  v-model.number="itemForm.unitPriceNet"
                  type="number"
                  min="0"
                  step="0.01"
                />
              </div>
              <div class="fg">
                <label>MwSt. (%)</label
                ><input
                  v-model.number="itemForm.taxPct"
                  type="number"
                  min="0"
                  max="100"
                />
              </div>
              <div class="fg">
                <label>Einheit</label
                ><input
                  v-model="itemForm.defaultUnit"
                  placeholder="h / Stk / pcs"
                />
              </div>
            </div>
            <div class="fg">
              <label>Status</label>
              <select v-model="itemForm.active">
                <option :value="true">Aktiv</option>
                <option :value="false">Inaktiv</option>
              </select>
            </div>
            <p v-if="itemFormErr" class="err">{{ itemFormErr }}</p>
          </div>
          <div class="modal-foot">
            <button class="btn-cancel" @click="itemModal = false">
              Abbrechen
            </button>
            <button class="btn-save" @click="saveItem" :disabled="savingItem">
              {{
                savingItem
                  ? "Speichern…"
                  : editItem?.id
                    ? "Speichern"
                    : "Anlegen"
              }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ══════════════ DELETE CONFIRM ══════════════ -->
    <Teleport to="body">
      <div v-if="delConfirm" class="overlay" @click.self="delConfirm = null">
        <div class="modal modal--sm">
          <div class="modal-head">
            <span class="modal-title">Wirklich löschen?</span>
            <button class="modal-x" @click="delConfirm = null">✕</button>
          </div>
          <div class="modal-body">
            <p class="del-msg">{{ delConfirm.msg }}</p>
          </div>
          <div class="modal-foot">
            <button class="btn-cancel" @click="delConfirm = null">
              Abbrechen
            </button>
            <button
              class="btn-del"
              @click="
                delConfirm.go();
                delConfirm = null;
              "
              :disabled="deleting"
            >
              {{ deleting ? "…" : "Endgültig löschen" }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- ══════════════ TOAST ══════════════ -->
    <Teleport to="body">
      <transition name="t">
        <div v-if="toast" class="toast" :class="`toast--${toast.type}`">
          {{ toast.message }}
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";

const API = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";

interface Customer {
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
interface Invoice {
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
interface Item {
  id: string;
  name: string;
  description: string | null;
  defaultUnit: string | null;
  defaultTaxRate: number;
  unitPriceNet: number;
  active: boolean;
}

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

const invSearch = ref("");
const invFilter = ref("Alle");
const invStatuses = [
  { val: "Alle", label: "Alle" },
  { val: "DRAFT", label: "Entwurf" },
  { val: "SENT", label: "Versendet" },
  { val: "PAID", label: "Bezahlt" },
  { val: "CANCELLED", label: "Storniert" },
];
const filteredInv = computed(() => {
  let l = invoices.value;
  const q = invSearch.value.toLowerCase();
  if (q)
    l = l.filter(
      (i) =>
        (i.invoiceNumber || "").toLowerCase().includes(q) ||
        `${i.customer?.forename} ${i.customer?.surname}`
          .toLowerCase()
          .includes(q),
    );
  if (invFilter.value !== "Alle")
    l = l.filter((i) => i.status === invFilter.value);
  return l;
});

const custSearch = ref("");
const filteredCust = computed(() => {
  const q = custSearch.value.toLowerCase();
  if (!q) return customers.value;
  return customers.value.filter((c) =>
    `${c.forename} ${c.surname} ${c.email} ${c.city}`.toLowerCase().includes(q),
  );
});

const itemSearch = ref("");
const itemFilter = ref<"all" | "active" | "inactive">("all");
const filteredItems = computed(() => {
  let l = items.value;
  const q = itemSearch.value.toLowerCase();
  if (q)
    l = l.filter(
      (i) =>
        (i.name || "").toLowerCase().includes(q) ||
        (i.description || "").toLowerCase().includes(q),
    );
  if (itemFilter.value === "active") l = l.filter((i) => i.active);
  if (itemFilter.value === "inactive") l = l.filter((i) => !i.active);
  return l;
});

const detailInv = ref<Invoice | null>(null);
function openInvDetail(inv: Invoice) {
  detailInv.value = inv;
}

const editInvModal = ref(false);
const savingInv = ref(false);
const invFormErr = ref("");
let editingInv: Invoice | null = null;
const invForm = reactive<{
  status: string;
  issueDate: string;
  serviceDate: string;
  notes: string;
  items: {
    title: string;
    quantity: number;
    unit: string;
    unitPriceNet: number;
    taxRatePct: number;
    itemId?: string | null;
  }[];
}>({ status: "DRAFT", issueDate: "", serviceDate: "", notes: "", items: [] });

function openEditInv(inv: Invoice) {
  editingInv = inv;
  invForm.status = inv.status || "DRAFT";
  invForm.issueDate = inv.issueDate || "";
  invForm.serviceDate = inv.serviceDate || "";
  invForm.notes = inv.notes || "";
  invForm.items = (inv.items || []).map((it) => ({
    title: it.title,
    quantity: it.quantity,
    unit: it.unit,
    unitPriceNet: it.unitPriceNet,
    taxRatePct: Math.round(it.taxRate * 100),
    itemId: it.itemId,
  }));
  invFormErr.value = "";
  editInvModal.value = true;
}

async function saveInv() {
  if (!editingInv) return;
  invFormErr.value = "";
  if (!invForm.items.length) {
    invFormErr.value = "Mindestens eine Position ist erforderlich.";
    return;
  }
  for (const it of invForm.items) {
    if (!it.title.trim()) {
      invFormErr.value = "Alle Positionen benötigen eine Bezeichnung.";
      return;
    }
  }
  savingInv.value = true;
  const payload = {
    customerId: editingInv.customer.id,
    issueDate: invForm.issueDate,
    serviceDate: invForm.serviceDate,
    currency: editingInv.currency || "EUR",
    notes: invForm.notes,
    status: invForm.status,
    items: invForm.items.map((it, i) => ({
      position: i + 1,
      itemId: it.itemId || null,
      title: it.title,
      quantity: Number(it.quantity),
      unit: it.unit || "pcs",
      unitPriceNet: Number(it.unitPriceNet),
      taxRate: Number(it.taxRatePct) / 100,
    })),
  };
  try {
    const res = await fetch(`${API}/invoices/${editingInv.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const e = await res
        .json()
        .catch(() => ({ message: `HTTP ${res.status}` }));
      throw new Error(e.message);
    }
    const updated: Invoice = await res.json();
    const idx = invoices.value.findIndex((i) => i.id === updated.id);
    if (idx >= 0) invoices.value.splice(idx, 1, updated);
    editInvModal.value = false;
    toast_("Rechnung gespeichert", "ok");
  } catch (e: any) {
    invFormErr.value = `Fehler: ${e.message}`;
  } finally {
    savingInv.value = false;
  }
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
    if (!res.ok) throw new Error();
    const created: Invoice = await res.json();
    invoices.value.unshift(created);
    toast_(`${created.invoiceNumber} dupliziert`, "ok");
  } catch {
    toast_("Fehler beim Duplizieren", "err");
  }
}

function printInv(inv: Invoice) {
  const rows = (inv.items || [])
    .map(
      (it) =>
        `<tr><td>${it.position}</td><td>${it.title}</td><td style="text-align:right">${it.quantity}</td><td>${it.unit}</td><td style="text-align:right">${fmtMoney(it.unitPriceNet)}</td><td style="text-align:right">${(it.taxRate * 100).toFixed(0)}%</td><td style="text-align:right">${fmtMoney(it.netTotal)}</td><td style="text-align:right">${fmtMoney(it.grossTotal)}</td></tr>`,
    )
    .join("");
  const html = `<!DOCTYPE html><html><head><meta charset="utf-8"><title>${inv.invoiceNumber}</title><style>body{font-family:Helvetica Neue,sans-serif;font-size:11px;padding:40px;color:#222}table{width:100%;border-collapse:collapse;margin-top:16px}th{background:#f0f0ec;padding:7px 8px;text-align:left;font-size:9px;text-transform:uppercase;letter-spacing:.4px;color:#666}td{padding:7px 8px;border-bottom:1px solid #f4f4f2}.grand{font-weight:700;font-size:12px;background:#f0f0ec}.r{text-align:right}@page{size:A4;margin:15mm}</style></head><body><h2 style="margin:0 0 4px">${inv.invoiceNumber}</h2><p style="color:#888;font-size:10px;margin:0">${inv.customer?.forename} ${inv.customer?.surname} · ${fmtDate(inv.issueDate)}</p><table><thead><tr><th>Pos.</th><th>Bezeichnung</th><th class="r">Menge</th><th>Einheit</th><th class="r">Netto/Einh.</th><th class="r">MwSt.</th><th class="r">Netto</th><th class="r">Brutto</th></tr></thead><tbody>${rows}</tbody></table><div style="display:flex;flex-direction:column;align-items:flex-end;margin-top:8px"><div style="display:flex;justify-content:space-between;width:220px;padding:3px 8px;font-size:10px;color:#666"><span>Netto</span><span>${fmtMoney(inv.netTotal)}</span></div><div style="display:flex;justify-content:space-between;width:220px;padding:3px 8px;font-size:10px;color:#666"><span>MwSt.</span><span>${fmtMoney(inv.taxTotal)}</span></div><div class="grand" style="display:flex;justify-content:space-between;width:220px;padding:5px 8px"><span>Gesamt (Brutto)</span><span>${fmtMoney(inv.grossTotal)}</span></div></div></body></html>`;
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

const delConfirm = ref<{ msg: string; go: () => void } | null>(null);
const deleting = ref(false);
function askDelete(type: string, name: string, action: () => void) {
  delConfirm.value = {
    msg: `${type} "${name}" wirklich unwiderruflich löschen?`,
    go: action,
  };
}
async function deleteInv(id: string) {
  deleting.value = true;
  try {
    await fetch(`${API}/invoices/${id}`, { method: "DELETE" });
    invoices.value = invoices.value.filter((i) => i.id !== id);
    toast_("Rechnung gelöscht", "ok");
  } catch {
    toast_("Fehler", "err");
  } finally {
    deleting.value = false;
  }
}

const custModal = ref(false);
const savingCust = ref(false);
const custFormErr = ref("");
const editCust = ref<Customer | null>(null);
const custForm = reactive({
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

function openCustomerForm(c?: Customer) {
  editCust.value = c || null;
  Object.assign(
    custForm,
    c
      ? {
          type: c.type,
          forename: c.forename,
          surname: c.surname,
          street: c.street,
          zip: c.zip,
          city: c.city,
          country: c.country,
          email: c.email,
          phone: c.phone,
          vatId: c.vatId || "",
        }
      : {
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
        },
  );
  custFormErr.value = "";
  custModal.value = true;
}

async function saveCust() {
  custFormErr.value = "";
  const req = [
    "forename",
    "surname",
    "street",
    "zip",
    "city",
    "email",
    "phone",
  ] as const;
  for (const f of req)
    if (!custForm[f].trim()) {
      custFormErr.value = `Feld "${f}" ist erforderlich.`;
      return;
    }
  savingCust.value = true;
  const body = { ...custForm, vatId: custForm.vatId || null };
  try {
    const url = editCust.value?.id
      ? `${API}/customers/${editCust.value.id}`
      : `${API}/customers`;
    const method = editCust.value?.id ? "PUT" : "POST";
    const res = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      const e = await res.json().catch(() => ({}));
      throw new Error(e.message ?? `HTTP ${res.status}`);
    }
    const saved: Customer = await res.json();
    if (editCust.value?.id) {
      const i = customers.value.findIndex((c) => c.id === saved.id);
      if (i >= 0) customers.value.splice(i, 1, saved);
    } else customers.value.unshift(saved);
    custModal.value = false;
    toast_(editCust.value?.id ? "Kunde aktualisiert" : "Kunde angelegt", "ok");
  } catch (e: any) {
    custFormErr.value = `Fehler: ${e.message}`;
  } finally {
    savingCust.value = false;
  }
}

async function deleteCust(id: string) {
  deleting.value = true;
  try {
    await fetch(`${API}/customers/${id}`, { method: "DELETE" });
    customers.value = customers.value.filter((c) => c.id !== id);
    toast_("Kunde gelöscht", "ok");
  } catch {
    toast_("Fehler", "err");
  } finally {
    deleting.value = false;
  }
}

const itemModal = ref(false);
const savingItem = ref(false);
const itemFormErr = ref("");
const editItem = ref<Item | null>(null);
const itemForm = reactive({
  name: "",
  description: "",
  defaultUnit: "",
  unitPriceNet: 0,
  taxPct: 19,
  active: true,
});

function openItemForm(item?: Item) {
  editItem.value = item || null;
  Object.assign(
    itemForm,
    item
      ? {
          name: item.name,
          description: item.description || "",
          defaultUnit: item.defaultUnit || "",
          unitPriceNet: item.unitPriceNet,
          taxPct: Math.round(item.defaultTaxRate * 100),
          active: item.active,
        }
      : {
          name: "",
          description: "",
          defaultUnit: "",
          unitPriceNet: 0,
          taxPct: 19,
          active: true,
        },
  );
  itemFormErr.value = "";
  itemModal.value = true;
}

async function saveItem() {
  itemFormErr.value = "";
  if (!itemForm.name.trim()) {
    itemFormErr.value = "Name ist erforderlich.";
    return;
  }
  savingItem.value = true;
  const body = {
    name: itemForm.name.trim(),
    description: itemForm.description.trim() || null,
    defaultUnit: itemForm.defaultUnit.trim() || null,
    unitPriceNet: itemForm.unitPriceNet,
    defaultTaxRate: itemForm.taxPct / 100,
    active: itemForm.active,
  };
  try {
    const url = editItem.value?.id
      ? `${API}/items/${editItem.value.id}`
      : `${API}/items`;
    const method = editItem.value?.id ? "PUT" : "POST";
    const res = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!res.ok) {
      const e = await res.json().catch(() => ({}));
      throw new Error(e.message ?? `HTTP ${res.status}`);
    }
    const saved: Item = await res.json();
    if (editItem.value?.id) {
      const i = items.value.findIndex((x) => x.id === saved.id);
      if (i >= 0) items.value.splice(i, 1, saved);
    } else items.value.unshift(saved);
    itemModal.value = false;
    toast_(
      editItem.value?.id ? "Artikel aktualisiert" : "Artikel angelegt",
      "ok",
    );
  } catch (e: any) {
    itemFormErr.value = `Fehler: ${e.message}`;
  } finally {
    savingItem.value = false;
  }
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
    if (!res.ok) throw new Error();
    const updated: Item = await res.json();
    const i = items.value.findIndex((x) => x.id === item.id);
    if (i >= 0) items.value.splice(i, 1, updated);
    toast_(`Artikel ${updated.active ? "aktiviert" : "deaktiviert"}`, "ok");
  } catch {
    toast_("Fehler", "err");
  }
}

const toast = ref<{ message: string; type: "ok" | "err" } | null>(null);
function toast_(message: string, type: "ok" | "err") {
  toast.value = { message, type };
  setTimeout(() => (toast.value = null), 3000);
}

function initials(a?: string, b?: string) {
  return `${(a || "").charAt(0)}${(b || "").charAt(0)}`.toUpperCase() || "?";
}
function fmtDate(d?: string) {
  if (!d) return "—";
  const [y, m, day] = d.split("-");
  return `${day}.${m}.${y}`;
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
function statusLabel(s?: string) {
  return (
    (
      {
        DRAFT: "Entwurf",
        SENT: "Versendet",
        PAID: "Bezahlt",
        CANCELLED: "Storniert",
      } as any
    )[s ?? ""] ??
    s ??
    "—"
  );
}
</script>

<style scoped>
.mgmt {
  min-height: 100vh;
  background: #f5f5f3;
  font-family: "Aspekta", "Helvetica Neue", sans-serif;
  padding: 32px 36px 80px;
  display: flex;
  flex-direction: column;
  gap: 22px;
}

/* ── tabs ── */
.tab-bar {
  display: flex;
  gap: 4px;
  background: #fff;
  border: 1px solid #e6e6e2;
  border-radius: 12px;
  padding: 5px;
  width: fit-content;
}
.tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 11px 24px;
  border: none;
  background: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #777;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.15s;
  white-space: nowrap;
}
.tab:hover {
  background: #f5f5f3;
  color: #333;
}
.tab--active {
  background: #111;
  color: #fff;
}
.tab--active .tab__icon {
  color: #fff;
}
.tab__icon {
  display: flex;
  color: #bbb;
  transition: color 0.15s;
}
.tab__count {
  font-size: 11px;
  font-weight: 700;
  background: rgba(0, 0, 0, 0.08);
  color: inherit;
  padding: 1px 7px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}
.tab--active .tab__count {
  background: rgba(255, 255, 255, 0.2);
}

/* ── toolbar ── */
.toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border: 1px solid #e6e6e2;
  border-radius: 8px;
  padding: 10px 14px;
  flex: 1;
  min-width: 180px;
  max-width: 340px;
}
.search-box input {
  border: none;
  outline: none;
  font-size: 14px;
  font-family: inherit;
  color: #222;
  width: 100%;
  background: transparent;
}
.search-box input::placeholder {
  color: #bbb;
}
.chips {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}
.chip {
  padding: 8px 15px;
  background: #fff;
  border: 1px solid #e6e6e2;
  border-radius: 20px;
  font-size: 13px;
  font-family: inherit;
  color: #666;
  cursor: pointer;
  transition: all 0.13s;
}
.chip:hover {
  border-color: #aaa;
  color: #222;
}
.chip--on {
  background: #111;
  border-color: #111;
  color: #fff;
}
.btn-add {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 10px 20px;
  background: #1a56db;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.13s;
  white-space: nowrap;
}
.btn-add:hover {
  background: #1447b8;
}

/* ── card / table ── */
.card {
  background: #fff;
  border: 1px solid #e6e6e2;
  border-radius: 12px;
  overflow: hidden;
}
.tbl {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.tbl thead tr {
  border-bottom: 1px solid #f0f0ec;
}
.tbl thead th {
  padding: 13px 16px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #aaa;
  text-align: left;
  white-space: nowrap;
}
.tbl tbody tr {
  border-bottom: 1px solid #f8f8f6;
  transition: background 0.1s;
}
.tbl tbody tr:last-child {
  border-bottom: none;
}
.tbl tbody tr:hover {
  background: #fafaf8;
}
.tbl tbody td {
  padding: 14px 16px;
  color: #333;
  vertical-align: middle;
}
.r {
  text-align: right !important;
}
.mono {
  font-variant-numeric: tabular-nums;
}
.muted {
  color: #999;
}
.bold {
  font-weight: 600;
  color: #111;
}
.sub {
  font-size: 12px;
  color: #bbb;
  margin-top: 2px;
}

/* ── name cell ── */
.name-cell {
  display: flex;
  align-items: center;
  gap: 11px;
}
.avatar {
  width: 38px;
  height: 38px;
  background: linear-gradient(135deg, #e8f0fd, #c7d7fa);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: #1a56db;
  flex-shrink: 0;
}

/* ── badges ── */
.badge {
  display: inline-block;
  padding: 4px 11px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}
.b-draft {
  background: #f3f4f6;
  color: #6b7280;
}
.b-sent {
  background: #eff6ff;
  color: #1d4ed8;
}
.b-paid {
  background: #f0fdf4;
  color: #16a34a;
}
.b-cancelled {
  background: #fef2f2;
  color: #dc2626;
}

/* ── ACTION BUTTONS — groß mit Beschriftung ── */
.acts {
  display: flex;
  gap: 6px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.act {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #f5f5f3;
  border: 1px solid #e0e0dc;
  border-radius: 8px;
  cursor: pointer;
  color: #444;
  font-size: 13px;
  font-weight: 500;
  font-family: inherit;
  transition: all 0.13s;
  white-space: nowrap;
  line-height: 1;
}
.act svg {
  flex-shrink: 0;
}
.act:hover {
  background: #e8e8e6;
  border-color: #c8c8c4;
  color: #111;
}
.act--del:hover {
  background: #fff1f1;
  border-color: #fca5a5;
  color: #dc2626;
}

.item-del {
  padding: 8px 12px;
  align-self: flex-end;
  margin-bottom: 2px;
}

/* ── empty / loader ── */
.empty {
  padding: 64px 24px;
  text-align: center;
  color: #bbb;
  font-size: 15px;
}
.loader {
  padding: 56px 24px;
  display: flex;
  justify-content: center;
}
.spin {
  width: 26px;
  height: 26px;
  border: 2.5px solid #f0f0ec;
  border-top-color: #1a56db;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ── modals ── */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}
.modal {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 8px 48px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 560px;
  max-height: 90vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}
.modal--lg {
  max-width: 860px;
}
.modal--sm {
  max-width: 420px;
}

.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22px 26px 16px;
  border-bottom: 1px solid #f0f0ec;
  flex-shrink: 0;
  gap: 12px;
}
.modal-head__left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.modal-head__right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.modal-title {
  font-size: 17px;
  font-weight: 700;
  color: #111;
  letter-spacing: -0.3px;
}
.modal-x {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #aaa;
  padding: 5px 9px;
  border-radius: 6px;
  transition: all 0.12s;
  flex-shrink: 0;
}
.modal-x:hover {
  color: #111;
  background: #f4f4f2;
}

.modal-body {
  padding: 20px 26px;
  flex: 1;
}
.modal-foot {
  padding: 16px 26px 22px;
  border-top: 1px solid #f0f0ec;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  flex-shrink: 0;
}

/* ── forms ── */
.fg {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-top: 14px;
}
.fg label {
  font-size: 11.5px;
  font-weight: 700;
  color: #aaa;
  text-transform: uppercase;
  letter-spacing: 0.4px;
}
.form-row {
  display: flex;
  gap: 12px;
}
.form-row .fg {
  flex: 1;
  min-width: 0;
}

.modal-body input,
.modal-body select,
.modal-body textarea {
  border: 1px solid #e2e2de;
  border-radius: 7px;
  padding: 11px 13px;
  font-size: 14px;
  font-family: inherit;
  color: #1a1a1a;
  background: #fff;
  width: 100%;
  box-sizing: border-box;
  outline: none;
  transition: border-color 0.13s;
}
.modal-body textarea {
  resize: vertical;
  min-height: 76px;
}
.modal-body input:focus,
.modal-body select:focus,
.modal-body textarea:focus {
  border-color: #111;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.05);
}

.section-label {
  font-size: 11.5px;
  font-weight: 700;
  color: #aaa;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  margin: 6px 0 10px;
  display: block;
}

/* ── position rows ── */
.item-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f3;
}
.item-pos {
  font-size: 12px;
  color: #ccc;
  min-width: 18px;
  text-align: center;
  padding-top: 24px;
  flex-shrink: 0;
}
.item-fields {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0;
}
.item-fields-row {
  display: flex;
  gap: 8px;
}
.item-fields-row .fg {
  flex: 1;
  min-width: 0;
}

.btn-ghost {
  margin-top: 12px;
  padding: 10px 18px;
  background: none;
  border: 1.5px dashed #d8d8d4;
  border-radius: 7px;
  font-size: 13px;
  font-family: inherit;
  color: #888;
  cursor: pointer;
  transition: all 0.14s;
}
.btn-ghost:hover {
  border-color: #111;
  color: #111;
}

/* ── invoice detail ── */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 8px;
}
.detail-label {
  font-size: 11px;
  font-weight: 700;
  color: #aaa;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  margin: 0 0 7px;
}
.detail-val {
  margin: 2px 0;
  font-size: 14px;
  color: #333;
}
.kv {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  padding: 4px 0;
  color: #555;
}
.totals-block {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-top: 12px;
  gap: 2px;
}
.total-row {
  display: flex;
  justify-content: space-between;
  gap: 48px;
  padding: 5px 10px;
  font-size: 13px;
  color: #666;
  width: 280px;
}
.total-row--grand {
  background: #f0f0ec;
  font-weight: 700;
  font-size: 14px;
  color: #111;
  border-radius: 5px;
  margin-top: 3px;
}
.notes-block {
  margin-top: 18px;
  font-size: 13px;
  color: #888;
  line-height: 1.6;
}

/* ── modal buttons ── */
.btn-sm {
  padding: 8px 16px;
  background: none;
  border: 1px solid #e2e2de;
  border-radius: 7px;
  font-size: 13px;
  font-family: inherit;
  color: #555;
  cursor: pointer;
  transition: background 0.12s;
}
.btn-sm:hover {
  background: #f4f4f2;
}
.btn-cancel {
  padding: 11px 20px;
  background: none;
  border: 1px solid #e2e2de;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  color: #555;
  cursor: pointer;
  transition: background 0.12s;
}
.btn-cancel:hover {
  background: #f4f4f2;
}
.btn-save {
  padding: 11px 24px;
  background: #1a56db;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  color: #fff;
  cursor: pointer;
  transition: background 0.12s;
}
.btn-save:hover {
  background: #1447b8;
}
.btn-save:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}
.btn-del {
  padding: 11px 24px;
  background: #dc2626;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  color: #fff;
  cursor: pointer;
  transition: background 0.12s;
}
.btn-del:hover {
  background: #b91c1c;
}
.btn-del:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.del-msg {
  font-size: 15px;
  color: #444;
  line-height: 1.55;
  margin: 0;
}
.err {
  margin-top: 14px;
  padding: 10px 14px;
  background: #fff5f5;
  border: 1px solid #fed7d7;
  border-radius: 7px;
  font-size: 13px;
  color: #dc2626;
}

/* ── toast ── */
.toast {
  position: fixed;
  bottom: 28px;
  right: 28px;
  padding: 13px 22px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  color: #fff;
  z-index: 9999;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
.toast--ok {
  background: #16a34a;
}
.toast--err {
  background: #dc2626;
}
.t-enter-active,
.t-leave-active {
  transition: all 0.22s ease;
}
.t-enter-from,
.t-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* ── responsive ── */
@media (max-width: 900px) {
  .mgmt {
    padding: 20px 16px 60px;
  }
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 640px) {
  .acts {
    flex-direction: column;
    align-items: flex-end;
  }
}
</style>
