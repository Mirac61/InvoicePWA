<template>
    <img src="" alt="">
   <div class="hp">
    <h1 class="hp-title">Rechnungen, schnell erledigt.</h1>
        <p class="hp-desc">Mit Invar Invoice Creator können Rechnungen in Handumdrehen erstellt werden.</p>
        <button class="get-started" @click="emit('get-started')">Jetzt starten!</button>
   </div>
</template>
<script setup lang="ts">
    const emit = defineEmits<{ (e: 'get-started'): void }>();
</script>
<style scoped>

.hp {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-top: 15%;
}

.hp-title {
  font-size: 2.5em;
  font-weight: 600;
  margin: 0 0 0.4em;
}

.hp-desc {
  font-size: 1em;
  color: #555;
  margin: 0 0 1.5em;
}


    .get-started {
        padding: 15px 30px;
        font-size: 1.5em;
        background-color: var(--primary-accent-color);
        color: var(--secondary-background-color);
        border: none;
        border-radius: 1px;
        cursor: pointer;

        font-size: 1rem;
        
    }

    .hp-title {
        font-size: 2.5em;
        margin-bottom: 0.5em;
    }
</style>