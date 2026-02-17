<script setup lang="ts">

</script>

<template>
    <nav class="navbar"> 
        <img src="/public/Invar.svg" alt="Invar Logo">

        <ul class="navbar__links">
            <li><a href="">Home</a></li>
            <li><a href="">Creator</a></li>
            <li><a href="">Einstellungen</a></li>
        </ul>
    </nav>
</template>

<style scoped>
.navbar {
    display: flex;
    align-items: center;
    padding: 1rem 2rem;
    background-color: var(--secondary-accent-color);
    align-items: center;
}

.navbar__links {
    list-style: none;
    display: flex;
    gap: 2rem;
    justify-content: center;
    margin-inline: auto;
    color: var(--primary-background-color);
}

a{
    text-decoration: none;
    color: var(--primary-background-color);
}
</style>
