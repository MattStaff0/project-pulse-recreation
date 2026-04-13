<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Teams</h1>
      <el-button type="primary" @click="$router.push('/teams/create')" v-if="userInfoStore.isAdmin">Create Team</el-button>
    </div>
    <el-input v-model="search" placeholder="Search teams..." style="max-width:300px;margin-bottom:16px" clearable />
    <el-table :data="filteredTeams" stripe v-loading="loading">
      <el-table-column prop="name" label="Team Name" sortable />
      <el-table-column prop="description" label="Description" show-overflow-tooltip />
      <el-table-column prop="sectionName" label="Section" />
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
import { ref, computed, onMounted } from 'vue'
import { getTeams } from '@/apis/team'
import { useUserInfoStore } from '@/stores/userInfo'

const userInfoStore = useUserInfoStore()
const teams = ref([])
const loading = ref(false)
const search = ref('')
const filteredTeams = computed(() => {
  if (!search.value) return teams.value
  return teams.value.filter(t => t.name.toLowerCase().includes(search.value.toLowerCase()))
})

onMounted(async () => {
  loading.value = true
  try { const res = await getTeams(); teams.value = res.data || [] } catch {} finally { loading.value = false }
})
</script>
