<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Instructors</h1>
      <el-button type="primary" @click="$router.push('/invite-instructors')">Invite Instructors</el-button>
    </div>

    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="6"><el-input v-model="filters.firstName" placeholder="First name" clearable /></el-col>
      <el-col :span="6"><el-input v-model="filters.lastName" placeholder="Last name" clearable /></el-col>
      <el-col :span="6"><el-input v-model="filters.teamName" placeholder="Team name" clearable /></el-col>
      <el-col :span="6">
        <el-select v-model="filters.status" placeholder="Status" style="width:100%" clearable>
          <el-option label="Active" value="active" />
          <el-option label="Deactivated" value="deactivated" />
        </el-select>
      </el-col>
    </el-row>

    <el-table :data="filteredInstructors" stripe v-loading="loading">
      <el-table-column prop="firstName" label="First Name" sortable />
      <el-table-column prop="lastName" label="Last Name" sortable />
      <el-table-column prop="email" label="Email" />
      <el-table-column label="Status" width="120">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'danger'">{{ row.enabled ? 'Active' : 'Deactivated' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Teams">
        <template #default="{ row }">
          <el-tag v-for="team in row.teams" :key="team.id" style="margin:2px">{{ team.name }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="240">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/instructors/${row.id}`)">View</el-button>
          <el-button type="warning" link @click="handleDeactivate(row.id)" v-if="row.enabled">Deactivate</el-button>
          <el-button type="success" link @click="handleReactivate(row.id)" v-else>Reactivate</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { deactivateInstructor, getInstructors, reactivateInstructor } from '@/apis/instructor'

const instructors = ref([])
const loading = ref(false)
const filters = ref({
  firstName: '',
  lastName: '',
  teamName: '',
  status: ''
})

const filteredInstructors = computed(() => {
  const firstName = filters.value.firstName.trim().toLowerCase()
  const lastName = filters.value.lastName.trim().toLowerCase()
  const teamName = filters.value.teamName.trim().toLowerCase()
  const status = filters.value.status

  const filtered = instructors.value.filter(instructor => {
    const teams = (instructor.teams || []).map(team => team.name).join(' ').toLowerCase()
    if (firstName && !instructor.firstName?.toLowerCase().includes(firstName)) return false
    if (lastName && !instructor.lastName?.toLowerCase().includes(lastName)) return false
    if (teamName && !teams.includes(teamName)) return false
    if (status === 'active' && !instructor.enabled) return false
    if (status === 'deactivated' && instructor.enabled) return false
    return true
  })

  return [...filtered].sort((left, right) => (left.lastName || '').localeCompare(right.lastName || ''))
})

async function load() {
  loading.value = true
  try {
    const response = await getInstructors()
    instructors.value = response.data || []
  } catch {} finally {
    loading.value = false
  }
}

onMounted(load)

async function handleDeactivate(id) {
  try {
    await deactivateInstructor(id)
    ElMessage.success('Instructor deactivated')
    load()
  } catch {}
}

async function handleReactivate(id) {
  try {
    await reactivateInstructor(id)
    ElMessage.success('Instructor reactivated')
    load()
  } catch {}
}
</script>
