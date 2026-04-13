<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Sections</h1>
      <el-button type="primary" @click="$router.push('/sections/create')">Create Section</el-button>
    </div>
    <el-input v-model="search" placeholder="Search by name..." style="max-width:300px;margin-bottom:16px" clearable />
    <el-table :data="filteredSections" stripe style="width:100%" v-loading="loading">
      <el-table-column prop="name" label="Section Name" sortable />
      <el-table-column prop="startDate" label="Start Date" />
      <el-table-column prop="endDate" label="End Date" />
      <el-table-column prop="numberOfTeams" label="Teams" width="80" />
      <el-table-column prop="numberOfStudents" label="Students" width="100" />
      <el-table-column label="Actions" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/sections/${row.id}`)">View</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getSections } from '@/apis/section'

const sections = ref([])
const loading = ref(false)
const search = ref('')

const filteredSections = computed(() => {
  if (!search.value) return sections.value
  return sections.value.filter(s => s.name.toLowerCase().includes(search.value.toLowerCase()))
})

onMounted(async () => {
  loading.value = true
  try { const res = await getSections(); sections.value = res.data || [] } catch {} finally { loading.value = false }
})
</script>
