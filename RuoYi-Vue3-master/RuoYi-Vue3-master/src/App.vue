<template>
  <router-view />
  <!-- AI客服悬浮窗 -->
  <AiChat />
</template>

<script setup>
import { nextTick, onMounted } from 'vue'
import useSettingsStore from '@/store/modules/settings'
import { handleThemeStyle } from '@/utils/theme'
import AiChat from '@/components/AiChat/index.vue'

onMounted(() => {
  nextTick(() => {
    // 初始化主题样式
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
