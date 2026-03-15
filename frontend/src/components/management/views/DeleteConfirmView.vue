<template>
  <Teleport to="body">
    <div
      v-if="confirm"
      class="fixed inset-0 bg-black/35 z-[1000] flex items-center justify-center p-4"
      @click.self="emit('cancel')"
    >
      <div
        class="bg-white rounded-2xl shadow-2xl w-full max-w-sm flex flex-col"
      >
        <div
          class="flex items-center justify-between px-6 py-5 border-b border-gray-100 shrink-0"
        >
          <span class="text-lg font-bold text-gray-900 tracking-tight"
            >Wirklich löschen?</span
          >
          <button
            class="text-gray-400 hover:text-gray-900 hover:bg-gray-100 px-2 py-1 rounded-lg transition-colors"
            @click="emit('cancel')"
          >
            ✕
          </button>
        </div>
        <div class="px-6 py-5">
          <p class="text-sm text-gray-600 leading-relaxed">{{ confirm.msg }}</p>
        </div>
        <div
          class="px-6 py-4 border-t border-gray-100 flex gap-2.5 justify-end shrink-0"
        >
          <button
            class="px-5 py-2.5 border border-gray-200 rounded-lg text-sm text-gray-600 hover:bg-gray-50 transition-colors"
            @click="emit('cancel')"
          >
            Abbrechen
          </button>
          <button
            class="px-6 py-2.5 bg-red-600 hover:bg-red-700 disabled:opacity-50 text-white rounded-lg text-sm font-semibold transition-colors"
            @click="emit('confirm')"
            :disabled="loading"
          >
            {{ loading ? "…" : "Endgültig löschen" }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
defineProps<{ confirm: { msg: string } | null; loading: boolean }>();
const emit = defineEmits<{ (e: "confirm"): void; (e: "cancel"): void }>();
</script>
