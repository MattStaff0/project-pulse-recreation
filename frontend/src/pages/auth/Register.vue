<template>
  <div style="display: flex; justify-content: center; align-items: center; height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
    <el-card style="width: 440px; border-radius: 12px" shadow="always">
      <template #header>
        <div style="text-align: center">
          <h2 style="margin: 0">Create Your Account</h2>
          <p style="color: #909399; margin-top: 8px">Complete your registration</p>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="Email">
          <el-input v-model="form.email" disabled />
        </el-form-item>
        <el-form-item label="First Name" prop="firstName">
          <el-input v-model="form.firstName" placeholder="First Name" />
        </el-form-item>
        <el-form-item label="Last Name" prop="lastName">
          <el-input v-model="form.lastName" placeholder="Last Name" />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="form.password" type="password" placeholder="Password" show-password />
        </el-form-item>
        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="Confirm Password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">Register</el-button>
        </el-form-item>
        <div style="text-align: center">
          <router-link to="/login" style="color: #409eff; text-decoration: none; font-size: 14px">Already have an account? Sign in</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerApi, validateInvitationApi } from '@/apis/login'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = ref({ email: '', firstName: '', lastName: '', password: '', confirmPassword: '', token: '' })

const rules = {
  firstName: [{ required: true, message: 'Required', trigger: 'blur' }],
  lastName: [{ required: true, message: 'Required', trigger: 'blur' }],
  password: [{ required: true, message: 'Required', trigger: 'blur' }, { min: 6, message: 'At least 6 characters', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: 'Required', trigger: 'blur' }, {
    validator: (rule, value, callback) => {
      if (value !== form.value.password) callback(new Error('Passwords do not match'))
      else callback()
    }, trigger: 'blur'
  }]
}

onMounted(async () => {
  const token = route.query.token
  const email = route.query.email
  if (token) {
    form.value.token = token
    try {
      const res = await validateInvitationApi(token)
      if (res.flag && res.data) {
        form.value.email = res.data.email || email || ''
        if (res.data.accepted) {
          ElMessage.warning('This invitation has already been used. Please log in.')
          router.push('/login')
        }
      }
    } catch {
      ElMessage.error('Invalid invitation link')
    }
  }
  if (email) form.value.email = email
})

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await registerApi(form.value)
    if (res.flag) {
      ElMessage.success('Registration successful! Please log in.')
      router.push('/login')
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.message || 'Registration failed')
  } finally {
    loading.value = false
  }
}
</script>
