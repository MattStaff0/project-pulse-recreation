<template>
  <el-container style="height: 100vh">
    <el-aside :width="isCollapsed ? '64px' : '220px'" style="transition: width 0.3s; background: #1d1e2c">
      <div style="padding: 16px; text-align: center; color: #fff; font-size: 18px; font-weight: bold; white-space: nowrap; overflow: hidden">
        {{ isCollapsed ? 'PP' : 'Project Pulse' }}
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="isCollapsed"
        background-color="#1d1e2c"
        text-color="#a0a4b8"
        active-text-color="#409eff"
        router
        :collapse-transition="false"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-menu-item :index="'/' + route.path" v-if="shouldShowRoute(route)">
            <el-icon><component :is="route.meta.icon" /></el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #eee; background: #fff">
        <div style="display: flex; align-items: center; gap: 12px">
          <el-button :icon="isCollapsed ? 'Expand' : 'Fold'" text @click="isCollapsed = !isCollapsed" />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">Home</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title && $route.name !== 'Home'">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div style="display: flex; align-items: center; gap: 16px">
          <el-dropdown @command="handleCommand">
            <span style="cursor: pointer; display: flex; align-items: center; gap: 6px">
              <el-avatar :size="32" style="background: #409eff">{{ userInitials }}</el-avatar>
              <span>{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">Profile</el-dropdown-item>
                <el-dropdown-item command="logout" divided>Logout</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main style="background: #f5f7fa; overflow-y: auto">
        <RouterView />
      </el-main>

      <el-footer style="text-align: center; color: #909399; font-size: 12px; border-top: 1px solid #eee; background: #fff">
        Project Pulse v1.0.0
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useTokenStore } from '@/stores/token'
import { useUserInfoStore } from '@/stores/userInfo'
import { useSettingsStore } from '@/stores/settings'

const router = useRouter()
const route = useRoute()
const tokenStore = useTokenStore()
const userInfoStore = useUserInfoStore()
const settingsStore = useSettingsStore()

const isCollapsed = ref(false)

const userName = computed(() => {
  const u = userInfoStore.userInfo
  return u ? `${u.firstName} ${u.lastName}` : ''
})

const userInitials = computed(() => {
  const u = userInfoStore.userInfo
  return u ? `${u.firstName?.[0] || ''}${u.lastName?.[0] || ''}` : ''
})

const menuRoutes = computed(() => {
  const dashboardRoute = router.getRoutes().find(r => r.path === '/')
  return dashboardRoute?.children?.filter(r => r.meta?.isMenuItem) || []
})

function shouldShowRoute(route) {
  if (!route.meta?.requiresPermissions) return true
  const userRoles = userInfoStore.roles
  if (userRoles.includes('admin')) return true
  return route.meta.requiresPermissions.some(p => userRoles.includes(p))
}

function handleCommand(cmd) {
  if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'logout') {
    tokenStore.clearToken()
    userInfoStore.clearUserInfo()
    settingsStore.clear()
    router.push('/login')
  }
}
</script>
