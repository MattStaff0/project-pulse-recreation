<template>
  <div class="page-container" v-loading="loading">
    <h1 class="page-title">Instructor: {{ instructor.firstName }} {{ instructor.lastName }}</h1>
    <el-descriptions :column="2" border style="margin-top:20px">
      <el-descriptions-item label="First Name">{{ instructor.firstName }}</el-descriptions-item>
      <el-descriptions-item label="Last Name">{{ instructor.lastName }}</el-descriptions-item>
      <el-descriptions-item label="Email">{{ instructor.email }}</el-descriptions-item>
      <el-descriptions-item label="Status">
        <el-tag :type="instructor.enabled ? 'success' : 'danger'">{{ instructor.enabled ? 'Active' : 'Deactivated' }}</el-tag>
      </el-descriptions-item>
    </el-descriptions>
    <h3 style="margin-top:24px">Supervised Teams</h3>
    <el-table :data="instructor.teams || []" stripe>
      <el-table-column prop="name" label="Team" />
      <el-table-column label="Actions" width="100">
        <template #default="{ row }"><el-button type="primary" link @click="$router.push(`/teams/${row.id}`)">View</el-button></template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getInstructorById } from '@/apis/instructor'

const route = useRoute()
const instructor = ref({})
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try { const res = await getInstructorById(route.params.id); instructor.value = res.data || {} } catch {} finally { loading.value = false }
})
</script>
