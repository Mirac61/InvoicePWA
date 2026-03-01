<script setup lang="ts">
import { computed, ref } from 'vue';
import Creator from './creator.vue';
import Settings from './Settings.vue';
import Home from './Home.vue';

const Components = {
    Home,
    Creator,
    Settings
};


const activeComponent = ref<keyof typeof Components>('Home');

const currentComponent = computed(() => {
    return Components[activeComponent.value];
});
</script>

<template>
    <nav class="navbar"> 
        <img src="/public/Invar.svg" alt="Invar Logo">

        <ul class="navbar__links">
           <li><a @click.prevent="activeComponent = 'Home'">Home</a></li>
            <li><a @click.prevent="activeComponent = 'Creator'">Creator</a></li>
            <li><a @click.prevent="activeComponent = 'Settings'">Einstellungen</a></li>
        </ul>
    </nav>
    <main>
        <component :is="currentComponent" />
    </main>
</template>

<style scoped>
.navbar {
    display: flex;
    align-items: center;
    padding: 1rem 2rem;
    background-color: var(--navbar-background-color);
    align-items: center;
}

.navbar__links{
    list-style: none;
    display: flex;
    gap: 2rem;
    justify-content: center;
    margin-inline: auto;
    color: var(--navbar-color);
    cursor: pointer;
}


a{
    text-decoration: none;
    color: var(--navbar-color);
}
</style>
