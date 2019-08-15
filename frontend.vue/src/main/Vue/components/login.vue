<template>
    <div v-if="userRole !== 'USER' || userRole !== 'ADMIN'">
        Login page
    </div>
</template>

<script>
    export default {
        name: "login",
        props: {
            'user-role': String
        }
    }
</script>

<style scoped>

</style>