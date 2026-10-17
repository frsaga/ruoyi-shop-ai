<template>
    <div style="text-align: center;">
      <img :src="data.img[index]" style="display: block; margin: 0 auto;">
      <div>
        <button @click="prev" v-if="index > 0">上一页</button>
        <button @click="next" v-if="index < data.img.length - 1">下一页</button>
      </div>
    </div>
  </template>
  <script setup lang="ts">
  import { ref,reactive } from 'vue'
  let index=ref(0)
  const data = reactive({
    img: ['/1.jpg','/2.jpg','/3.jpg'],
    show:true
  })
  
  const prev = () => {
    if (index.value > 0) {
      index.value--
    }
  }
  
  const next = () => {
    if (index.value < data.img.length - 1) {
      index.value++
    }
  }
  </script>

  