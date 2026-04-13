<template>
  <div class="page-container">
    <h1 class="page-title">My Profile</h1>
    <el-card style="max-width: 600px; margin-top: 20px; border-radius: 12px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="First Name"><el-input v-model="form.firstName" /></el-form-item>
        <el-form-item label="Last Name"><el-input v-model="form.lastName" /></el-form-item>
        <el-form-item label="Email"><el-input v-model="form.email" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">Save Changes</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="max-width: 600px; margin-top: 20px; border-radius: 12px">
      <h3 style="margin-bottom: 16px">Change Password</h3>
      <el-form :model="pwForm" label-width="160px">
        <el-form-item label="Current Password"><el-input v-model="pwForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="New Password"><el-input v-model="pwForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item>
          <el-button type="warning" @click="handleChangePassword" :loading="changingPw">Change Password</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/stores/userInfo'
import { updateUser, changePassword } from '@/apis/user'

const userInfoStore = useUserInfoStore()
const saving = ref(false)
const changingPw = ref(false)
const form = ref({ firstName: '', lastName: '', email: '' })
const pwForm = ref({ oldPassword: '', newPassword: '' })

onMounted(() => {
  const u = userInfoStore.userInfo
  if (u) { form.value = { firstName: u.firstName, lastName: u.lastName, email: u.email } }
})

async function handleSave() {
  saving.value = true
  try {
    const res = await updateUser(userInfoStore.userInfo.id, form.value)
    if (res.flag) {
      userInfoStore.setUserInfo({ ...userInfoStore.userInfo, ...form.value })
      ElMessage.success('Profile updated')
    }
  } catch { /* handled */ } finally { saving.value = false }
}

async function handleChangePassword() {
  changingPw.value = true
  try {
    const res = await changePassword(userInfoStore.userInfo.id, pwForm.value.oldPassword, pwForm.value.newPassword)
    if (res.flag) {
      ElMessage.success('Password changed')
      pwForm.value = { oldPassword: '', newPassword: '' }
    }
  } catch { /* handled */ } finally { changingPw.value = false }
}
</script>
