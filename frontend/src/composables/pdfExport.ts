import { nextTick } from "vue";
import { activeFontOption, getFontUrl } from "./fontSelection";

export async function downloadPDF() {
  await nextTick();

  const el = document.getElementById("invoice-preview");
  if (!el) return;

  const font = activeFontOption();

  // Only grab creator.css — all invoice styles live there
  const creatorStylesheet = Array.from(
    document.querySelectorAll<HTMLLinkElement>('link[rel="stylesheet"]')
  ).find((link) => link.href.includes("creator"))?.outerHTML ?? "";

  // Inline <style> tags (Vue scoped styles)
  const inlineStyles = Array.from(document.querySelectorAll("style"))
    .map((s) => s.outerHTML)
    .join("\n");

  const win = window.open("", "_blank", "width=900,height=1200");
  if (!win) return;

  win.document.write(`
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="utf-8" />
        <title></title>

        <!-- Google Font for this invoice -->
        <link rel="stylesheet" href="${getFontUrl(font)}" />

        <!-- App styles -->
        ${creatorStylesheet}
        ${inlineStyles}

        <style>
          * {
            -webkit-print-color-adjust: exact !important;
            print-color-adjust: exact !important;
            color-adjust: exact !important;
          }

          :root{
            --invoice-font: ${font.family};
          }
          body {
            margin: 0;
            background: white;
            /* Apply the selected font to everything in the print window */
            font-family: ${font.family};
          }
          .invoice-preview {
            font-family: ${font.family};
          }
          @page { margin: 15mm; size: A4; }
        </style>
      </head>
      <body>${el.outerHTML}</body>
    </html>
  `);

  win.document.close();
  win.focus();

  // Give Google Fonts time to load before printing
  win.onload = () => {
    // Small extra delay ensures font is rendered, not just loaded
    setTimeout(() => {
      win.print();
      win.close();
    }, 300);
  };

  // Fallback
  setTimeout(() => {
    if (!win.closed) {
      win.print();
      win.close();
    }
  }, 1500);
}