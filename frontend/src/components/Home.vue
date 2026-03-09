<script lang="ts" setup>
import { CirclePlus } from 'lucide-vue-next';
</script>
<template>
    <h1 class="saved-invoices__title">Deine gespeicherten Rechnungen</h1>
    <div class="new-invoice__button">
        <div class="new-invoice__button-content">
            <CirclePlus  />
            <p class="invoice__button-text">Neue Rechnung</p>
        </div>
    </div>
</template> 
<style scoped>
.saved-invoices__title {
    font-size: 1.5rem;
    margin: 2rem 7.5rem;
    font-weight: 400;
}

.new-invoice__button {
    display: flex;
    background-color: var(--primary-background-color);
    padding: 8rem 4.6rem;
    border-radius: 1rem;
    border: 2px dashed var(--dotted-border);
    width: fit-content;
    margin: 0 0 0 7.5rem;
    cursor: pointer;
}


.new-invoice__button-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.04rem;
}
</style>