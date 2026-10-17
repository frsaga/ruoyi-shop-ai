<template>
  <div class="navbar" :class="{ 'theme-dark': settingsStore.sideTheme === 'theme-dark', 'theme-light': settingsStore.sideTheme === 'theme-light' }">
    <hamburger id="hamburger-container" :is-active="appStore.sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />
    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!settingsStore.topNav" />
    <top-nav id="topmenu-container" class="topmenu-container" v-if="settingsStore.topNav" />

    <div class="right-menu">
      <template v-if="appStore.device !== 'mobile'">
        <header-search id="header-search" class="right-menu-item" />

        <el-tooltip content="源码地址" effect="dark" placement="bottom">
          <ruo-yi-git id="ruoyi-git" class="right-menu-item hover-effect" />
        </el-tooltip>

        <el-tooltip content="文档地址" effect="dark" placement="bottom">
          <ruo-yi-doc id="ruoyi-doc" class="right-menu-item hover-effect" />
        </el-tooltip>

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template>
      <div class="avatar-container">
        <el-dropdown @command="handleCommand" class="right-menu-item hover-effect" trigger="click">
          <div class="avatar-wrapper">
            <img :src="userStore.avatar" class="user-avatar" />
            <el-icon><caret-bottom /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <router-link to="/user/profile">
                <el-dropdown-item>个人中心</el-dropdown-item>
              </router-link>
              <el-dropdown-item command="setLayout" v-if="settingsStore.showSettings">
                <span>布局设置</span>
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import HeaderSearch from '@/components/HeaderSearch'
import RuoYiGit from '@/components/RuoYi/Git'
import RuoYiDoc from '@/components/RuoYi/Doc'
import useAppStore from '@/store/modules/app'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'

const appStore = useAppStore()
const userStore = useUserStore()
const settingsStore = useSettingsStore()

function toggleSideBar() {
  appStore.toggleSideBar()
}

function handleCommand(command) {
  switch (command) {
    case "setLayout":
      setLayout();
      break;
    case "logout":
      logout();
      break;
    default:
      break;
  }
}

function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logOut().then(() => {
      location.href = '/index';
    })
  }).catch(() => { });
}

const emits = defineEmits(['setLayout'])
function setLayout() {
  emits('setLayout');
}
</script>

<style lang='scss' scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  // 渐变背景，与侧边栏风格一致
  background: linear-gradient(135deg, 
    rgba(255, 255, 255, 0.98) 0%, 
    rgba(248, 250, 252, 0.98) 100%);
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);

  .hamburger-container {
    line-height: 50px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: all 0.3s ease;
    -webkit-tap-highlight-color: transparent;
    padding: 0 15px;
    border-radius: 0 8px 8px 0;

    &:hover {
      background: rgba(102, 126, 234, 0.1);
      transform: translateX(2px);
    }
  }

  .breadcrumb-container {
    float: left;
    margin-left: 10px;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;
    display: flex;
    align-items: center;
    padding-right: 10px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0 10px;
      height: 36px;
      font-size: 18px;
      color: rgba(90, 94, 102, 0.9);
      vertical-align: middle;
      border-radius: 8px;
      transition: all 0.3s ease;
      margin: 0 2px;

      &.hover-effect {
        cursor: pointer;

        &:hover {
          background: rgba(102, 126, 234, 0.1);
          color: rgba(102, 126, 234, 1);
          transform: translateY(-2px);
        }
      }
    }

    .avatar-container {
      margin-right: 20px;
      margin-left: 10px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 4px 8px;
        border-radius: 12px;
        transition: all 0.3s ease;
        cursor: pointer;

        &:hover {
          background: rgba(102, 126, 234, 0.1);
        }

        .user-avatar {
          cursor: pointer;
          width: 36px;
          height: 36px;
          border-radius: 10px;
          border: 2px solid rgba(102, 126, 234, 0.2);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;

          &:hover {
            border-color: rgba(102, 126, 234, 0.5);
            transform: scale(1.05);
          }
        }

        i {
          cursor: pointer;
          font-size: 12px;
          color: rgba(90, 94, 102, 0.7);
          transition: all 0.3s ease;
        }

        &:hover i {
          color: rgba(102, 126, 234, 1);
        }
      }
    }
  }
}

// 暗色主题适配
.navbar.theme-dark {
  background: linear-gradient(135deg, 
    rgba(48, 65, 86, 0.98) 0%, 
    rgba(31, 45, 61, 0.98) 100%);
  border-bottom-color: rgba(255, 255, 255, 0.1);
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);

  .hamburger-container {
    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
  }

  .right-menu {
    .right-menu-item {
      color: rgba(255, 255, 255, 0.8);

      &.hover-effect {
        &:hover {
          background: rgba(255, 255, 255, 0.1);
          color: rgba(255, 255, 255, 1);
        }
      }
    }

    .avatar-container {
      .avatar-wrapper {
        &:hover {
          background: rgba(255, 255, 255, 0.1);
        }

        .user-avatar {
          border-color: rgba(255, 255, 255, 0.2);

          &:hover {
            border-color: rgba(255, 255, 255, 0.4);
          }
        }

        i {
          color: rgba(255, 255, 255, 0.7);
        }

        &:hover i {
          color: rgba(255, 255, 255, 1);
        }
      }
    }
  }
}
</style>
