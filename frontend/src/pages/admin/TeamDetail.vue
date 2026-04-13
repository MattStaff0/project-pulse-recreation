<template>
  <div class="page-container" v-loading="loading">
    <div class="page-header">
      <h1 class="page-title">Team: {{ team.name }}</h1>
      <div v-if="userInfoStore.isAdmin">
        <el-button @click="$router.push(`/teams/${id}/edit`)">Edit</el-button>
        <el-popconfirm title="Delete this team?" @confirm="handleDelete">
          <template #reference><el-button type="danger">Delete</el-button></template>
        </el-popconfirm>
      </div>
    </div>
    <el-descriptions :column="2" border style="margin-bottom:24px">
      <el-descriptions-item label="Name">{{ team.name }}</el-descriptions-item>
      <el-descriptions-item label="Description">{{ team.description }}</el-descriptions-item>
      <el-descriptions-item label="Website">{{ team.websiteUrl }}</el-descriptions-item>
      <el-descriptions-item label="Section">{{ team.sectionName }}</el-descriptions-item>
    </el-descriptions>
    <h3>Students</h3>
    <el-table :data="team.students || []" stripe style="margin-bottom:24px">
      <el-table-column prop="firstName" label="First Name" />
      <el-table-column prop="lastName" label="Last Name" />
      <el-table-column prop="email" label="Email" />
      <el-table-column label="Actions" width="150" v-if="userInfoStore.isAdmin">
        <template #default="{ row }">
          <el-popconfirm title="Remove from team?" @confirm="handleRemoveStudent(row.id)">
            <template #reference><el-button type="danger" link>Remove</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <h3>Instructors</h3>
    <el-table :data="team.instructors || []" stripe>
      <el-table-column prop="firstName" label="First Name" />
      <el-table-column prop="lastName" label="Last Name" />
      <el-table-column label="Actions" width="150" v-if="userInfoStore.isAdmin">
        <template #default="{ row }">
          <el-popconfirm title="Remove from team?" @confirm="handleRemoveInstructor(row.id)">
            <template #reference><el-button type="danger" link>Remove</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTeamById, deleteTeam, removeStudentFromTeam, removeInstructorFromTeam } from '@/apis/team'
import { useUserInfoStore } from '@/stores/userInfo'

const route = useRoute()
const router = useRouter()
const userInfoStore = useUserInfoStore()
const id = route.params.id
const team = ref({})
const loading = ref(false)

async function load() {
  loading.value = true
  try { const res = await getTeamById(id); team.value = res.data || {} } catch {} finally { loading.value = false }
}
onMounted(load)

async function handleDelete() {
  try { await deleteTeam(id); ElMessage.success('Team deleted'); router.push('/teams') } catch {}
}

async function handleRemoveStudent(studentId) {
  try { await removeStudentFromTeam(id, studentId); ElMessage.success('Student removed'); load() } catch {}
}

async function handleRemoveInstructor(instructorId) {
  try { await removeInstructorFromTeam(id, instructorId); ElMessage.success('Instructor removed'); load() } catch {}
}
</script>
