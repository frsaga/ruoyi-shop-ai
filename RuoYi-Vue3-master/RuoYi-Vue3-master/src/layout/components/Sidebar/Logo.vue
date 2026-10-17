<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse, 'theme-dark': sideTheme === 'theme-dark', 'theme-light': sideTheme === 'theme-light' }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1 v-else class="sidebar-title">{{ title }}</h1>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo" />
        <h1 class="sidebar-title">{{ title }}</h1>
      </router-link>
    </transition>
  </div>
</template>

<script setup>
import variables from '@/assets/styles/variables.module.scss'
import logo from '@/assets/logo/logo.png'
import useSettingsStore from '@/store/modules/settings'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const title = import.meta.env.VITE_APP_TITLE;
const settingsStore = useSettingsStore();
const sideTheme = computed(() => settingsStore.sideTheme);
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  // 与侧边栏一致的渐变背景
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.95) 0%, 
    rgba(118, 75, 162, 0.95) 100%);
  text-align: center;
  overflow: hidden;
  border-radius: 0 30px 0 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 12px;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }

    & .sidebar-logo {
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 12px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.1);
      }
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: rgba(255, 255, 255, 0.98);
      font-weight: 600;
      line-height: 50px;
      font-size: 15px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      letter-spacing: 0.5px;
      background: linear-gradient(135deg, 
        rgba(255, 255, 255, 1) 0%, 
        rgba(255, 255, 255, 0.9) 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
    
    .sidebar-title {
      display: none;
    }
  }
}

// 暗色主题适配
.sidebar-logo-container.theme-dark {
  background: linear-gradient(135deg, 
    rgba(48, 65, 86, 0.98) 0%, 
    rgba(31, 45, 61, 0.98) 100%);
  border-bottom-color: rgba(255, 255, 255, 0.15);
}

// 亮色主题适配
.sidebar-logo-container.theme-light {
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.98) 0%, 
    rgba(248, 250, 252, 0.98) 100%);
  border-bottom-color: rgba(0, 0, 0, 0.1);
  
  .sidebar-logo-link {
    .sidebar-title {
      background: linear-gradient(135deg, 
        rgba(102, 126, 234, 1) 0%, 
        rgba(118, 75, 162, 1) 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      text-shadow: none;
    }
    
    &:hover {
      background: rgba(102, 126, 234, 0.1);
    }
  }
}
</style>