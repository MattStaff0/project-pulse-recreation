<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Teams</h1>
      <el-button type="primary" @click="$router.push('/teams/create')" v-if="userInfoStore.isAdmin">Create Team</el-button>
    </div>

    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="8"><el-input v-model="filters.teamName" placeholder="Team name" clearable /></el-col>
      <el-col :span="8"><el-input v-model="filters.sectionName" placeholder="Section name" clearable /></el-col>
      <el-col :span="8"><el-input v-model="filters.instructorName" placeholder="Instructor name" clearable /></el-col>
    </el-row>

    <el-table :data="filteredTeams" stripe v-loading="loading">
      <el-table-column prop="name" label="Team Name" sortable />
      <el-table-column prop="description" label="Description" show-overflow-tooltip />
      <el-table-column prop="sectionName" label="Section" />
      <el-table-column label="Instructors">
        <template #default="{ row }">
          <span>{{ row.instructorNames?.join(', ') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="numberOfStudents" label="Students" width="90" />
      <el-table-column label="Actions" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/teams/${row.id}`)">View</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getTeams } from '@/apis/team'
import { useUserInfoStore } from '@/stores/userInfo'

const userInfoStore = useUserInfoStore()
const teams = ref([])
const loading = ref(false)
const filters = ref({
  teamName: '',
  sectionName: '',
  instructorName: ''
})

const filteredTeams = computed(() => {
  const teamName = filters.value.teamName.trim().toLowerCase()
  const sectionName = filters.value.sectionName.trim().toLowerCase()
  const instructorName = filters.value.instructorName.trim().toLowerCase()

  const filtered = teams.value.filter(team => {
    const instructors = (team.instructorNames || []).join(' ').toLowerCase()
    if (teamName && !team.name?.toLowerCase().includes(teamName)) return false
    if (sectionName && !team.sectionName?.toLowerCase().includes(sectionName)) return false
    if (instructorName && !instructors.includes(instructorName)) return false
    return true
  })

  return [...filtered].sort((left, right) => {
    const sectionCompare = (right.sectionName || '').localeCompare(left.sectionName || '')
    if (sectionCompare !== 0) return sectionCompare
    return (left.name || '').localeCompare(right.name || '')
  })
})

onMounted(async () => {
  loading.value = true
  try {
    const response = await getTeams()
    teams.value = response.data || []
  } catch {} finally {
    loading.value = false
  }
})
</script>
