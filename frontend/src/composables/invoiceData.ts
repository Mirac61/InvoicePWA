import { reactive, computed } from "vue";

export interface TableColumn {
  label: string;
  align: "left" | "center" | "right";
}

export interface TableRow {
  cells: string[];
  linkedItemId?: string | null;
}

export interface InvoiceData {
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

export const invoiceData = reactive<InvoiceData>({
  logo: "",
  logoName: "",
  companyName: "Das Invar Unternehmen GmbH",
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
    "Das Invar Unternehmen GmbH | Musterstraße 21, 73728 Esslingen am Neckar | logo@mail.de | Tel.: 0737 281204",
});

// Restore persisted settings
const saved = localStorage.getItem("invar_doc_settings");
if (saved) Object.assign(invoiceData, JSON.parse(saved));

// ── Computed totals ────────────────────────────────────────────────────────

export const subtotal = computed(() =>
  invoiceData.tableRows.reduce((sum, row) => {
    const raw = (row.cells[row.cells.length - 1] ?? "")
      .replace(/[^\d,.-]/g, "")
      .replace(",", ".");
    return sum + (parseFloat(raw) || 0);
  }, 0),
);

export const discountAmount = computed(
  () => subtotal.value * (invoiceData.discountRate / 100),
);

export const afterDiscount = computed(
  () => subtotal.value - discountAmount.value,
);

export const taxAmount = computed(
  () => afterDiscount.value * (invoiceData.taxRate / 100),
);

export const grandTotal = computed(
  () => afterDiscount.value + (invoiceData.showTax ? taxAmount.value : 0),
);

// ── Table helpers ──────────────────────────────────────────────────────────

export function addColumn() {
  invoiceData.tableColumns.push({ label: "Neu", align: "left" });
  invoiceData.tableRows.forEach((r) => r.cells.push(""));
}

export function removeColumn(ci: number) {
  invoiceData.tableColumns.splice(ci, 1);
  invoiceData.tableRows.forEach((r) => r.cells.splice(ci, 1));
}

export function addRow() {
  invoiceData.tableRows.push({
    cells: invoiceData.tableColumns.map(() => ""),
    linkedItemId: null,
  });
}

export function removeRow(ri: number) {
  invoiceData.tableRows.splice(ri, 1);
}

// ── Helpers ────────────────────────────────────────────────────────────────

export function formatDate(d: string): string {
  if (!d) return "";
  const [y, m, day] = d.split("-");
  return `${day}.${m}.${y}`;
}

export function formatCurrency(n: number): string {
  return (
    n.toLocaleString("de-DE", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + "€"
  );
}
