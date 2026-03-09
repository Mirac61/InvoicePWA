import { api } from "./_http";
import type { CustomerResponse } from "./CustomerAPI";

export type InvoiceStatus = "DRAFT" | "SENT" | "PAID" | "CANCELLED";

export interface InvoiceItemResponse {
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

export interface InvoiceResponse {
  id: string;
  invoiceNumber: string;
  customer: CustomerResponse;
  issueDate: string;
  serviceDate: string;
  currency: string;
  notes: string;
  netTotal: number;
  taxTotal: number;
  grossTotal: number;
  status: InvoiceStatus;
  items: InvoiceItemResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateInvoiceItemRequest {
  position: number;
  itemId?: string | null;
  title: string;
  quantity: number;
  unit?: string;
  unitPriceNet: number;
  taxRate: number;
}

export interface CreateInvoiceRequest {
  customerId: string;
  issueDate: string;
  serviceDate?: string;
  currency: string;
  notes?: string;
  status: InvoiceStatus;
  items: CreateInvoiceItemRequest[];
}

export async function listInvoices(): Promise<InvoiceResponse[]> {
  return api<InvoiceResponse[]>("/invoices");
}

export async function getInvoice(id: string): Promise<InvoiceResponse> {
  return api<InvoiceResponse>(`/invoices/${encodeURIComponent(id)}`);
}

export async function createInvoice(
  payload: CreateInvoiceRequest,
): Promise<InvoiceResponse> {
  return api<InvoiceResponse>("/invoices", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function updateInvoice(
  id: string,
  payload: CreateInvoiceRequest,
): Promise<InvoiceResponse> {
  return api<InvoiceResponse>(`/invoices/${encodeURIComponent(id)}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  });
}

export async function deleteInvoice(id: string): Promise<void> {
  return api<void>(`/invoices/${encodeURIComponent(id)}`, {
    method: "DELETE",
  });
}
