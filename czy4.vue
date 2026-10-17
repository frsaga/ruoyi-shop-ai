<template>
    <img :src="state.imgUrl" alt="">
    </template>
    <script setup lang="ts">
    import { reactive } from 'vue'
    const state = reactive({
        imgUrl: '/1.jpg'
    })
    </script>
    <style scoped>
    img {
        width: 200px;
        height: 300px;
    }
    </style>