import { ref, computed, nextTick } from "vue";

export interface SectionDef {
  id: string;
  type: string;
  label: string;
  icon: string;
}

const ALL_SECTION_TYPES: Omit<SectionDef, "id">[] = [
  { type: "header", label: "Unternehmen", icon: "" },
  { type: "customer", label: "Kundendaten", icon: "" },
  { type: "meta", label: "Rechnungsinfo", icon: "" },
  { type: "greeting", label: "Anrede", icon: "" },
  { type: "table", label: "Tabelle", icon: "" },
  { type: "banking", label: "Bankverbindung", icon: "" },
  { type: "footer", label: "Abschluss", icon: "" },
];

let _id = 0;
const uid = () => `s-${++_id}`;

export const sections = ref<SectionDef[]>([
  { id: uid(), type: "header", label: "Unternehmen", icon: "" },
  { id: uid(), type: "customer", label: "Kundendaten", icon: "" },
  { id: uid(), type: "meta", label: "Rechnungsinfo", icon: "" },
  { id: uid(), type: "greeting", label: "Anrede", icon: "" },
  { id: uid(), type: "table", label: "Tabelle", icon: "" },
  { id: uid(), type: "banking", label: "Bankverbindung", icon: "" },
  { id: uid(), type: "footer", label: "Abschluss", icon: "" },
]);

export const activeSection = ref<string | null>(null);
export const dragIndex = ref<number | null>(null);
export const dragOverIndex = ref<number | null>(null);

export const availableSectionsToAdd = computed(() =>
  ALL_SECTION_TYPES.filter(
    (t) => !sections.value.some((s) => s.type === t.type),
  ),
);

export function toggleSection(id: string) {
  activeSection.value = activeSection.value === id ? null : id;
}

export function removeSection(id: string) {
  sections.value = sections.value.filter((s) => s.id !== id);
  if (activeSection.value === id) activeSection.value = null;
}

export function addSection(type: string) {
  const tmpl = ALL_SECTION_TYPES.find((t) => t.type === type);
  if (!tmpl) return;
  const s: SectionDef = { id: uid(), ...tmpl };
  sections.value.push(s);
  nextTick(() => {
    activeSection.value = s.id;
  });
}

export function onDragStart(e: DragEvent, idx: number) {
  dragIndex.value = idx;
  if (e.dataTransfer) e.dataTransfer.effectAllowed = "move";
}

export function onDragOver(_e: DragEvent, idx: number) {
  dragOverIndex.value = idx;
}

export function onDrop(_e: DragEvent, toIdx: number) {
  const from = dragIndex.value;
  if (from !== null && from !== toIdx) {
    const arr = [...sections.value];
    const [moved] = arr.splice(from, 1);
    arr.splice(toIdx, 0, moved);
    sections.value = arr;
  }
  onDragEnd();
}

export function onDragEnd() {
  dragIndex.value = null;
  dragOverIndex.value = null;
}
