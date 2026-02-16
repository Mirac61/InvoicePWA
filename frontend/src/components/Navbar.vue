<script setup lang="ts">
import { computed } from "vue";

type NavKey = "start" | "invoices" | "customers" | "positions" | "settings";

const props = defineProps<{
  active?: NavKey;
}>();

const emit = defineEmits<{
  navigate: [key: NavKey];
}>();

const activeKey = computed<NavKey>(() => props.active ?? "start");

// Navigation items for the navbar
const navItems: Array<{ key: NavKey; label: string }> = [
  { key: "start", label: "Dashboard" },
  { key: "invoices", label: "Invoices" },
  { key: "customers", label: "Customers" },
  { key: "positions", label: "Items" },
  { key: "settings", label: "Settings" },
];

// Handles navigation event
function navigateTo(key: NavKey) {
  emit("navigate", key);
}
</script>

<template>
  <header class="navbar" role="banner">
    <div class="navbar-left">
      <div class="app-name" aria-label="App Name">Invoice Manager</div>
      <span class="badge" aria-label="Status">Beta</span>
    </div>

    <div class="navbar-center">
      <nav class="nav-pill" aria-label="Main navigation">
        <button
          v-for="item in navItems"
          :key="item.key"
          class="nav-tab"
          :class="{ 'is-active': item.key === activeKey }"
          type="button"
          :aria-current="item.key === activeKey ? 'page' : undefined"
          @click="navigateTo(item.key)"
        >
          {{ item.label }}
        </button>
      </nav>
    </div>

    <div class="navbar-right">
      <button class="button-primary" type="button" @click="navigateTo('invoices')">
        New Invoice
      </button>
    </div>
  </header>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #0b1220;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

/* Left and right sections have fixed width to keep the center truly centered */
.navbar-left,
.navbar-right {
  flex: 0 0 220px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.navbar-right {
  justify-content: flex-end;
}

.app-name {
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.2px;
  color: #ffffff;
  white-space: nowrap;
}

.badge {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.85);
  white-space: nowrap;
}

/* Center section takes remaining space and centers the navigation pill */
.navbar-center {
  flex: 1 1 auto;
  display: flex;
  justify-content: center;
}

/* Navigation pill remains visually compact and precisely centered */
.nav-pill {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.06);
  overflow-x: auto;
  scrollbar-width: none;
  max-width: 720px;
  min-height: 48px;
}
.nav-pill::-webkit-scrollbar {
  display: none;
}

.nav-tab {
  appearance: none;
  border: 0;
  background: transparent;
  color: rgba(255, 255, 255, 0.78);
  height: 32px;
  padding: 0 14px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 32px;
  -webkit-font-smoothing: antialiased;
}

.nav-tab:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.nav-tab.is-active {
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
}

.button-primary {
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

.button-primary:hover {
  opacity: 0.92;
}

@media (max-width: 900px) {
  .navbar-left,
  .navbar-right {
    flex: 0 0 180px;
  }
  .nav-pill {
    max-width: 560px;
  }
}

@media (max-width: 720px) {
  .badge {
    display: none;
  }
  .navbar-left,
  .navbar-right {
    flex: 0 0 auto;
    min-width: 0;
  }
  .navbar-center {
    justify-content: flex-start;
  }
  .nav-pill {
    max-width: 100%;
  }
  .button-primary {
    padding: 10px 10px;
  }
}
</style>
