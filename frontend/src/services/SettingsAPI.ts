export interface SettingsResponse {
  id: string;

  companyName: string;
  contactName?: string;
  accountHolder?: string;
  registerCourt?: string;
  registerNumber?: string;
  owner?: string;
  managingDirector?: string;
  legalRepresentative?: string;

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

  invoicePrefix: string;
  nextInvoiceNo: number;
  paymentDays: number;
  defaultVatRate: number;

  invoiceFooter?: string;

  createdAt: string;
  updatedAt: string;
}

export interface UpdateSettingsRequest {
  companyName: string;
  contactName?: string;
  accountHolder?: string;
  registerCourt?: string;
  registerNumber?: string;
  owner?: string;
  managingDirector?: string;
  legalRepresentative?: string;

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

  invoicePrefix: string;
  nextInvoiceNo: number;
  paymentDays: number;
  defaultVatRate: number;

  invoiceFooter?: string;
}

async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`/api${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `HTTP ${res.status}`);
  }

  const contentType = res.headers.get("content-type") || "";
  if (!contentType.includes("application/json")) return null as unknown as T;

  return (await res.json()) as T;
}

export async function getSettings(): Promise<SettingsResponse> {
  return api<SettingsResponse>("/settings");
}

export async function updateSettings(
  payload: UpdateSettingsRequest,
): Promise<SettingsResponse> {
  return api<SettingsResponse>("/settings", {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}
