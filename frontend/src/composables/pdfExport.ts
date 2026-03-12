import { nextTick } from "vue";

export async function downloadPDF() {
  await nextTick();

  const el = document.getElementById("invoice-preview");
  if (!el) return;

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
          @page { margin: 15mm; size: A4; }
        </style>
      </head>
      <body>${el.outerHTML}</body>
    </html>
  `);

  win.document.close();
  win.focus();

  win.onload = () => {
    win.print();
    win.close();
  };

  setTimeout(() => {
    if (!win.closed) {
      win.print();
      win.close();
    }
  }, 500);
}
