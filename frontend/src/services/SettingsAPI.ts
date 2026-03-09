import { api } from "./_http";

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
