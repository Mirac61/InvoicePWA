export function initials(a?: string, b?: string): string {
  return `${(a || "").charAt(0)}${(b || "").charAt(0)}`.toUpperCase() || "?";
}

export function fmtDate(d?: string): string {
  if (!d) return "—";
  const [y, m, day] = d.split("-");
  return `${day}.${m}.${y}`;
}

export function fmtMoney(n?: number): string {
  if (n == null) return "—";
  return (
    n.toLocaleString("de-DE", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + " €"
  );
}

export function statusLabel(s?: string): string {
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
