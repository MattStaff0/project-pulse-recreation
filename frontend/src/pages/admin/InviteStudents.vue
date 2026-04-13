<template>
  <div class="page-container">
    <h1 class="page-title">Invite Students</h1>
    <el-card style="max-width:600px;margin-top:20px;border-radius:12px">
      <el-form label-position="top">
        <el-form-item label="Student Emails (separated by semicolons)">
          <el-input v-model="emails" type="textarea" :rows="4" placeholder="john@tcu.edu; jane@tcu.edu" />
        </el-form-item>
        <p style="color:#909399;font-size:13px;margin-bottom:16px">{{ emailCount }} email(s) detected</p>
        <el-form-item>
          <el-button type="primary" @click="handleSend" :loading="sending">Send Invitations</el-button>
          <el-button @click="$router.back()">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { inviteStudents } from '@/apis/invitation'
import { useUserInfoStore } from '@/stores/userInfo'

const route = useRoute()
const router = useRouter()
const userInfoStore = useUserInfoStore()
const emails = ref('')
const sending = ref(false)
const emailCount = computed(() => emails.value.split(';').filter(e => e.trim()).length)

async function handleSend() {
  if (!emails.value.trim()) { ElMessage.warning('Enter at least one email'); return }
  sending.value = true
  try {
    await inviteStudents({
      emails: emails.value,
      sectionId: route.params.id,
      senderName: `${userInfoStore.userInfo.firstName} ${userInfoStore.userInfo.lastName}`
    })
    ElMessage.success('Invitations sent!')
    router.back()
  } catch {} finally { sending.value = false }
}
</script>
