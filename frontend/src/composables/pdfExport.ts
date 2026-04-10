import { nextTick } from "vue";
import html2pdf from "html2pdf.js";
import { invoiceData } from "./invoiceData"; // Adjust the path as necessary

export async function downloadPDF() {
  await nextTick();

  const el = document.getElementById("invoice-preview");
  if (!el) return;

  const invoiceNumber = invoiceData.invoiceNumber || "Neu";

 const opt = {
    margin:       [15, 15, 15, 15], 
    filename:     `Rechnung_${invoiceNumber}.pdf`,
    image:        { type: 'jpeg' as const, quality: 0.98 },
    html2canvas:  { scale: 2, useCORS: true }, 
    jsPDF:        { 
      unit: 'mm' as const, 
      format: 'a4' as const, 
      orientation: 'portrait' as const 
    }
  };

  html2pdf().set(opt).from(el).save();
}