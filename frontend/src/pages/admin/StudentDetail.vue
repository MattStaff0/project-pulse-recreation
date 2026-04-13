<template>
  <div class="page-container" v-loading="loading">
    <h1 class="page-title">Student: {{ student.firstName }} {{ student.lastName }}</h1>
    <el-descriptions :column="2" border style="margin-top:20px">
      <el-descriptions-item label="First Name">{{ student.firstName }}</el-descriptions-item>
      <el-descriptions-item label="Last Name">{{ student.lastName }}</el-descriptions-item>
      <el-descriptions-item label="Email">{{ student.email }}</el-descriptions-item>
      <el-descriptions-item label="Section">{{ student.sectionName }}</el-descriptions-item>
      <el-descriptions-item label="Team">{{ student.teamName || 'Unassigned' }}</el-descriptions-item>
    </el-descriptions>
    <div style="margin-top:20px">
      <el-button @click="$router.push(`/student-activities/${student.id}`)" v-if="userInfoStore.isInstructor">View Activities</el-button>
      <el-button @click="$router.push(`/student-eval-report/${student.id}`)" v-if="userInfoStore.isInstructor">View Eval Report</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStudentById } from '@/apis/student'
import { useUserInfoStore } from '@/stores/userInfo'

const route = useRoute()
const userInfoStore = useUserInfoStore()
const student = ref({})
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try { const res = await getStudentById(route.params.id); student.value = res.data || {} } catch {} finally { loading.value = false }
})
</script>
