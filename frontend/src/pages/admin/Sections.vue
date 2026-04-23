<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Sections</h1>
      <el-button type="primary" @click="$router.push('/sections/create')">Create Section</el-button>
    </div>

    <el-input v-model="search" placeholder="Search by section name..." style="max-width:320px;margin-bottom:16px" clearable />

    <el-table :data="filteredSections" stripe style="width:100%" v-loading="loading">
      <el-table-column prop="name" label="Section Name" sortable />
      <el-table-column label="Teams">
        <template #default="{ row }">{{ row.teamNames?.join(', ') || '-' }}</template>
      </el-table-column>
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
import { computed, onMounted, ref } from 'vue'
import { getSections } from '@/apis/section'

const sections = ref([])
const loading = ref(false)
const search = ref('')

const filteredSections = computed(() => {
  const filtered = !search.value
    ? sections.value
    : sections.value.filter(section => section.name.toLowerCase().includes(search.value.toLowerCase()))

  return [...filtered].sort((left, right) => right.name.localeCompare(left.name))
})

onMounted(async () => {
  loading.value = true
  try {
    const response = await getSections()
    sections.value = response.data || []
  } catch {} finally {
    loading.value = false
  }
})
</script>
