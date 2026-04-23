<template>
  <div class="page-container">
    <div class="page-header"><h1 class="page-title">Students</h1></div>

    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="6"><el-input v-model="filters.firstName" placeholder="First name" clearable /></el-col>
      <el-col :span="6"><el-input v-model="filters.lastName" placeholder="Last name" clearable /></el-col>
      <el-col :span="6"><el-input v-model="filters.email" placeholder="Email" clearable /></el-col>
      <el-col :span="6"><el-input v-model="filters.sectionName" placeholder="Section name" clearable /></el-col>
    </el-row>
    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="6"><el-input v-model="filters.teamName" placeholder="Team name" clearable /></el-col>
    </el-row>

    <el-table :data="filteredStudents" stripe v-loading="loading">
      <el-table-column prop="firstName" label="First Name" sortable />
      <el-table-column prop="lastName" label="Last Name" sortable />
      <el-table-column prop="email" label="Email" />
      <el-table-column prop="teamName" label="Team" />
      <el-table-column prop="sectionName" label="Section" />
      <el-table-column label="Actions" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/students/${row.id}`)">View</el-button>
          <el-popconfirm title="Delete this student permanently?" @confirm="handleDelete(row.id)" v-if="userInfoStore.isAdmin">
            <template #reference><el-button type="danger" link>Delete</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { deleteStudent, getStudents } from '@/apis/student'
import { useUserInfoStore } from '@/stores/userInfo'

const userInfoStore = useUserInfoStore()
const students = ref([])
const loading = ref(false)
const filters = ref({
  firstName: '',
  lastName: '',
  email: '',
  sectionName: '',
  teamName: ''
})

const filteredStudents = computed(() => {
  const firstName = filters.value.firstName.trim().toLowerCase()
  const lastName = filters.value.lastName.trim().toLowerCase()
  const email = filters.value.email.trim().toLowerCase()
  const sectionName = filters.value.sectionName.trim().toLowerCase()
  const teamName = filters.value.teamName.trim().toLowerCase()

  const filtered = students.value.filter(student => {
    if (firstName && !student.firstName?.toLowerCase().includes(firstName)) return false
    if (lastName && !student.lastName?.toLowerCase().includes(lastName)) return false
    if (email && !student.email?.toLowerCase().includes(email)) return false
    if (sectionName && !student.sectionName?.toLowerCase().includes(sectionName)) return false
    if (teamName && !student.teamName?.toLowerCase().includes(teamName)) return false
    return true
  })

  return [...filtered].sort((left, right) => {
    const sectionCompare = (right.sectionName || '').localeCompare(left.sectionName || '')
    if (sectionCompare !== 0) return sectionCompare
    return (left.lastName || '').localeCompare(right.lastName || '')
  })
})

async function load() {
  loading.value = true
  try {
    const response = await getStudents()
    students.value = response.data || []
  } catch {} finally {
    loading.value = false
  }
}

onMounted(load)

async function handleDelete(id) {
  try {
    await deleteStudent(id)
    ElMessage.success('Student deleted')
    load()
  } catch {}
}
</script>
