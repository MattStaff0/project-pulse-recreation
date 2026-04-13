<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
    <el-card style="width: 400px; border-radius: 12px">
      <template #header><div style="text-align:center"><h2 style="margin:0">Reset Password</h2></div></template>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="New Password" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleReset" :loading="loading" style="width:100%">Reset Password</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { resetPasswordApi } from '@/apis/login'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ newPassword: '', confirmPassword: '' })
const rules = {
  newPassword: [{ required: true, message: 'Required', trigger: 'blur' }, { min: 6, message: 'At least 6 chars', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: 'Required', trigger: 'blur' }, {
    validator: (r, v, cb) => { v !== form.value.newPassword ? cb(new Error('Passwords must match')) : cb() }, trigger: 'blur'
  }]
}

async function handleReset() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await resetPasswordApi(route.query.email, route.query.token, form.value.newPassword)
    ElMessage.success('Password reset successfully!')
    router.push('/login')
  } catch (err) {
    ElMessage.error(err.response?.data?.message || 'Reset failed')
  } finally { loading.value = false }
}
</script>
