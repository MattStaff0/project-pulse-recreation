<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
    <el-card style="width: 400px; border-radius: 12px" shadow="always">
      <template #header>
        <div style="text-align: center">
          <h2 style="margin: 0; color: #303133">Project Pulse</h2>
          <p style="color: #909399; margin-top: 8px">Sign in to your account</p>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="Email" prefix-icon="Message" />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="form.password" type="password" placeholder="Password" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">Sign In</el-button>
        </el-form-item>
        <div style="text-align: center">
          <router-link to="/forgot-password" style="color: #409eff; text-decoration: none; font-size: 14px">Forgot password?</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginApi } from '@/apis/login'
import { useTokenStore } from '@/stores/token'
import { useUserInfoStore } from '@/stores/userInfo'

const router = useRouter()
const tokenStore = useTokenStore()
const userInfoStore = useUserInfoStore()

const formRef = ref(null)
const loading = ref(false)
const form = ref({ email: '', password: '' })
const rules = {
  email: [{ required: true, message: 'Email is required', trigger: 'blur' }],
  password: [{ required: true, message: 'Password is required', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await loginApi(form.value.email, form.value.password)
    if (res.flag) {
      tokenStore.setToken(res.data.token)
      userInfoStore.setUserInfo(res.data.userInfo)
      ElMessage.success('Login successful')
      router.push('/')
    } else {
      ElMessage.error(res.message || 'Login failed')
    }
  } catch (err) {
    ElMessage.error('Email or password is incorrect')
  } finally {
    loading.value = false
  }
}
</script>
