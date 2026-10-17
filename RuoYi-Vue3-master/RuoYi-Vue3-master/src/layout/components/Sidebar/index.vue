<template>
  <div class="sidebar-container" :class="{ 'has-logo': showLogo }">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar :class="sideTheme" wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
        :text-color="sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="true"
        :active-text-color="theme"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
        />
        <!-- AI客服菜单项 -->
        <el-menu-item 
          index="ai-chat" 
          class="ai-chat-menu-item"
          @click="handleAiChatClick"
        >
          <el-icon class="menu-icon">
            <ChatDotRound />
          </el-icon>
          <template #title>
            <span class="menu-title">AI客服</span>
            <el-badge 
              v-if="!isCollapse && unreadCount > 0" 
              :value="unreadCount" 
              :max="99" 
              class="menu-badge"
            />
          </template>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/assets/styles/variables.module.scss'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'
import useAiChatStore from '@/store/modules/aiChat'
import { ChatDotRound } from '@element-plus/icons-vue'

const route = useRoute();
const appStore = useAppStore()
const settingsStore = useSettingsStore()
const permissionStore = usePermissionStore()
const aiChatStore = useAiChatStore()

const sidebarRouters =  computed(() => permissionStore.sidebarRouters);
const showLogo = computed(() => settingsStore.sidebarLogo);
const sideTheme = computed(() => settingsStore.sideTheme);
const theme = computed(() => settingsStore.theme);
const isCollapse = computed(() => !appStore.sidebar.opened);
const unreadCount = computed(() => aiChatStore.unreadCount);

const activeMenu = computed(() => {
  const { meta, path } = route;
  // if set path, the sidebar will highlight the path you set
  if (meta.activeMenu) {
    return meta.activeMenu;
  }
  return path;
})

// 处理AI客服菜单点击
const handleAiChatClick = () => {
  aiChatStore.openChat()
}

</script>
