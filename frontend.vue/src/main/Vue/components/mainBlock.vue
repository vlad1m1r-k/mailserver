<template>
    <div :class="{'blur': userRole !== 'USER' || userRole !== 'ADMIN'}">
        PAGE content
    </div>
</template>

<script>
    export default {
        name: "mainBlock",
        props: {
            'user-role': String
        }
    }
</script>

<style scoped>
    .blur {
        filter: blur(5px);
    }
</style>