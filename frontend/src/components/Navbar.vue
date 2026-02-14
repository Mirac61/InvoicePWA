<!-- frontend/src/components/Navbar.vue -->
<script setup lang="ts">
import { computed } from "vue";

type NavKey = "home" | "invoices" | "customers" | "settings";

const props = defineProps<{
  active?: NavKey;
}>();

const emit = defineEmits<{
  navigate: [key: NavKey];
}>();

const activeKey = computed<NavKey>(() => props.active ?? "home");

const items: Array<{ key: NavKey; label: string }> = [
  { key: "home", label: "Start" },
  { key: "invoices", label: "Rechnungen" },
  { key: "customers", label: "Kunden" },
  { key: "settings", label: "Einstellungen" },
];

function go(key: NavKey) {
  emit("navigate", key);
}
</script>

<template>
  <header class="navigations-leiste" role="banner">
    <div class="leisten-links">
      <div class="app-name" aria-label="App Name">Rechnung</div>
      <span class="badge" aria-label="Status">Beta</span>
    </div>

    <nav class="nav" aria-label="Hauptnavigation">
      <button
        v-for="it in items"
        :key="it.key"
        class="tab"
        :class="{ active: it.key === activeKey }"
        type="button"
        :aria-current="it.key === activeKey ? 'page' : undefined"
        @click="go(it.key)"
      >
        {{ it.label }}
      </button>
    </nav>

    <div class="right">
      <button class="primary" type="button" @click="go('invoices')">
        Neue Rechnung
      </button>
    </div>
  </header>
</template>

<style scoped>
.navigations-leiste {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 16px;
  background: #0b1220;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.leisten-links {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 160px;
}

.app-name {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.2px;
  color: #ffffff;
}

.badge {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.85);
}

.nav {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.06);
  overflow-x: auto;
  scrollbar-width: none;
}
.nav::-webkit-scrollbar {
  display: none;
}

.tab {
  appearance: none;
  border: 0;
  background: transparent;
  color: rgba(255, 255, 255, 0.78);
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
  white-space: nowrap;
}

.tab:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.tab.active {
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
}

.right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-width: 160px;
}

.primary {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: #ffffff;
  color: #0b1220;
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}

.primary:hover {
  opacity: 0.92;
}

@media (max-width: 720px) {
  .badge {
    display: none;
  }
  .primary {
    padding: 10px 10px;
  }
}
</style>
