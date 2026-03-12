import {ref, watch} from 'vue';

export interface FontOption {
    label: string;
    family: string;
    googleName: string;
    weights: string;

}


export const fontOptions: FontOption[] = [
  {
    label:     "Aspekta",
    family:    "'Aspekta', 'sans-serif'",
    googleName: "Aspekta",
    weights:   "300;400;600;700",
  },
      {
    label:      "Lato",
    family:     "'Lato', sans-serif",
    googleName: "Lato",
    weights:    "300;400;600;700",
  },
  {
    label:      "Nunito",
    family:     "'Nunito', sans-serif",
    googleName: "Nunito",
    weights:    "300;400;600;700",
  },
  {
    label:      "Playfair Display",
    family:     "'Playfair Display', serif",
    googleName: "Playfair+Display",
    weights:    "400;600;700",
  },
  {
    label:      "Source Serif 4",
    family:     "'Source Serif 4', serif",
    googleName: "Source+Serif+4",
    weights:    "300;400;600;700",
  },
  {
    label:      "Inter",
    family:     "'Inter', sans-serif",
    googleName: "Inter",
    weights:    "300;400;600;700",
  },
  {
    label:      "DM Sans",
    family:     "'DM Sans', sans-serif",
    googleName: "DM+Sans",
    weights:    "300;400;600;700",
  },
  {
    label:      "Crimson Pro",
    family:     "'Crimson Pro', serif",
    googleName: "Crimson+Pro",
    weights:    "300;400;600;700",
  },
  {
    label:      "Josefin Sans",
    family:     "'Josefin Sans', sans-serif",
    googleName: "Josefin+Sans",
    weights:    "300;400;600;700",
  },
];

const storedFont = localStorage.getItem("invar_invoice_font") ?? "Lato";
export const selectedFont = ref<string>(storedFont);
 
// Build the Google Fonts URL for a given font option
export function getFontUrl(opt: FontOption): string {
  return `https://fonts.googleapis.com/css2?family=${opt.googleName}:wght@${opt.weights}&display=swap`;
}
 
// The currently active FontOption object
export const activeFontOption = () =>
  fontOptions.find(f => f.label === selectedFont.value) ?? fontOptions[0];
 
// Inject / update the Google Fonts <link> tag in the main document head
function injectFontLink(opt: FontOption) {
  const id = "invar-invoice-font";
  let link = document.getElementById(id) as HTMLLinkElement | null;
  if (!link) {
    link = document.createElement("link");
    link.id = id;
    link.rel = "stylesheet";
    document.head.appendChild(link);
  }
  link.href = getFontUrl(opt);
}
 
// Persist + update live preview whenever selection changes
watch(
  selectedFont,
  (val) => {
    localStorage.setItem("invar_invoice_font", val);
    const opt = fontOptions.find(f => f.label === val);
    if (opt) injectFontLink(opt);
  },
  { immediate: true }
);