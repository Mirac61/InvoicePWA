# Invoice API – Frontend Developer Reference

Base URL: `http://localhost:8080/api`

---

## Conventions

### Tax Rate
Tax rates are passed and returned as **decimal fractions**:
| Display | Value to send |
|---------|--------------|
| 19 %    | `0.19`       |
| 7 %     | `0.07`       |
| 0 %     | `0.0`        |

### Dates
All date fields are plain `String` values in **ISO 8601** format: `YYYY-MM-DD` (e.g. `"2024-03-15"`).

### Timestamps (`createdAt`, `updatedAt`)
Set automatically by the server. Format: ISO 8601 instant, e.g. `"2024-03-15T10:30:00Z"`.

### Error Responses
All errors return a consistent JSON body:
```json
{
  "timestamp": "2024-03-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invoice items must not be empty."
}
```
Validation errors additionally contain a `"details"` array with per-field messages.

| Status | When |
|--------|------|
| 200    | OK |
| 201    | Created |
| 204    | Deleted |
| 400    | Validation error / business rule violation |
| 404    | Resource not found |
| 500    | Unexpected server error |

---

## Endpoints

### GET /invoices
Returns all invoices.

**Response 200**
```json
[
  {
    "id": "a1b2c3d4-...",
    "invoiceNumber": "INV-000001",
    "customer": { ... },
    "issueDate": "2024-03-15",
    "serviceDate": "2024-03-15",
    "currency": "EUR",
    "notes": "Optional notes",
    "netTotal": 100.00,
    "taxTotal": 19.00,
    "grossTotal": 119.00,
    "status": "DRAFT",
    "items": [ ... ],
    "createdAt": "2024-03-15T10:00:00Z",
    "updatedAt": "2024-03-15T10:00:00Z"
  }
]
```

---

### GET /invoices/{id}
Returns a single invoice by ID.

**Response 200** — same shape as above.
**Response 404** — invoice not found.

---

### POST /invoices
Creates a new invoice. The `invoiceNumber` is generated automatically by the server.

**Request Body**
```json
{
  "customerId": "customer-uuid",
  "issueDate": "2024-03-15",
  "serviceDate": "2024-03-15",
  "currency": "EUR",
  "notes": "Optional",
  "status": "DRAFT",
  "items": [
    {
      "position": 1,
      "itemId": null,
      "title": "Web Development",
      "quantity": 8.0,
      "unit": "h",
      "unitPriceNet": 90.00,
      "taxRate": 0.19
    }
  ]
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `customerId` | yes | Must reference an existing customer |
| `issueDate` | yes | |
| `currency` | yes | e.g. `"EUR"`, `"USD"` |
| `status` | yes | e.g. `"DRAFT"`, `"SENT"`, `"PAID"` |
| `items` | yes | At least one item required |
| `items[].position` | yes | Sort order, integer |
| `items[].title` | yes | |
| `items[].quantity` | yes | Must be > 0 |
| `items[].unitPriceNet` | yes | Must be >= 0 |
| `items[].taxRate` | yes | Decimal fraction, must be >= 0 |
| `items[].unit` | no | Defaults to `"pcs"` |
| `items[].itemId` | no | Reference to catalogue item |

**Response 201** — the created invoice (full object including generated `invoiceNumber` and `items`).

---

### PUT /invoices/{id}
Replaces an existing invoice entirely. All items are replaced.

**Request Body** — same shape as POST.

**Response 200** — the updated invoice.
**Response 404** — invoice not found.

---

### DELETE /invoices/{id}
Deletes an invoice.

**Response 204** — deleted.
**Response 404** — not found.

---

## Object Schemas

### CustomerResponse
```json
{
  "id": "uuid",
  "type": "INDIVIDUAL",
  "forename": "Max",
  "surname": "Mustermann",
  "street": "Musterstraße 1",
  "zip": "12345",
  "city": "Berlin",
  "country": "DE",
  "email": "max@example.com",
  "phone": "+49 30 123456",
  "vatId": "DE123456789",
  "createdAt": "...",
  "updatedAt": "..."
}
```

### InvoiceItemResponse
```json
{
  "id": "invi_uuid",
  "position": 1,
  "itemId": null,
  "title": "Web Development",
  "quantity": 8.0,
  "unit": "h",
  "unitPriceNet": 90.00,
  "taxRate": 0.19,
  "netTotal": 720.00,
  "taxTotal": 136.80,
  "grossTotal": 856.80
}
```

---

## Status Values
There is currently no enforced enum — use the following strings by convention:

| Value | Meaning |
|-------|---------|
| `DRAFT`    | Invoice is being prepared |
| `SENT`     | Invoice has been sent to the customer |
| `PAID`     | Invoice has been paid |
| `CANCELLED`| Invoice has been cancelled |
