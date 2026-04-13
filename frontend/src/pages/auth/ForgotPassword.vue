<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
    <el-card style="width: 400px; border-radius: 12px">
      <template #header>
        <div style="text-align: center"><h2 style="margin:0">Forgot Password</h2></div>
      </template>
      <el-form :model="form" ref="formRef" label-position="top">
        <el-form-item label="Email" prop="email" :rules="[{ required: true, message: 'Email required', trigger: 'blur' }]">
          <el-input v-model="form.email" placeholder="Enter your email" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading" style="width:100%">Send Reset Link</el-button>
        </el-form-item>
        <div style="text-align:center"><router-link to="/login" style="color:#409eff;text-decoration:none;font-size:14px">Back to Login</router-link></div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { forgotPasswordApi } from '@/apis/login'

const formRef = ref(null)
const loading = ref(false)
const form = ref({ email: '' })

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await forgotPasswordApi(form.value.email)
    ElMessage.success('If the email exists, a reset link has been sent.')
  } catch { /* handled */ } finally { loading.value = false }
}
</script>
