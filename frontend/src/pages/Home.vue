<template>
  <div class="page-container">
    <h1 class="page-title">Welcome to Project Pulse</h1>
    <p style="color: #606266; margin: 16px 0">Hello, {{ userInfoStore.userInfo?.firstName }}! Use the sidebar to navigate.</p>

    <el-row :gutter="20" style="margin-top: 24px">
      <el-col :span="8" v-if="userInfoStore.isAdmin">
        <el-card shadow="hover" style="cursor:pointer; border-radius: 12px" @click="$router.push('/sections')">
          <el-statistic title="Sections" :value="stats.sections" />
        </el-card>
      </el-col>
      <el-col :span="8" v-if="userInfoStore.isInstructor || userInfoStore.isAdmin">
        <el-card shadow="hover" style="cursor:pointer; border-radius: 12px" @click="$router.push('/teams')">
          <el-statistic title="Teams" :value="stats.teams" />
        </el-card>
      </el-col>
      <el-col :span="8" v-if="userInfoStore.isInstructor || userInfoStore.isAdmin">
        <el-card shadow="hover" style="cursor:pointer; border-radius: 12px" @click="$router.push('/students')">
          <el-statistic title="Students" :value="stats.students" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserInfoStore } from '@/stores/userInfo'
import { getSections } from '@/apis/section'
import { getTeams } from '@/apis/team'
import { getStudents } from '@/apis/student'

const userInfoStore = useUserInfoStore()
const stats = ref({ sections: 0, teams: 0, students: 0 })

onMounted(async () => {
  try {
    if (userInfoStore.isInstructor || userInfoStore.isAdmin) {
      const [secRes, teamRes, stuRes] = await Promise.all([getSections(), getTeams(), getStudents()])
      stats.value.sections = secRes.data?.length || 0
      stats.value.teams = teamRes.data?.length || 0
      stats.value.students = stuRes.data?.length || 0
    }
  } catch { /* ok */ }
})
</script>
