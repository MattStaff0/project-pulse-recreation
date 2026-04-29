<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Activities - {{ studentName }}</h1>
      <el-button @click="$router.back()">Back</el-button>
    </div>

    <el-select v-model="selectedWeek" placeholder="Week" style="margin-bottom:16px;width:220px" @change="load">
      <el-option v-for="week in availableWeeks" :key="week" :label="`Week ${week}`" :value="week" />
    </el-select>

    <el-table :data="activities" stripe v-loading="loading">
      <el-table-column prop="category" label="Category" width="140">
        <template #default="{ row }"><el-tag>{{ row.category }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="activity" label="Activity" />
      <el-table-column prop="description" label="Description" show-overflow-tooltip />
      <el-table-column prop="plannedHours" label="Planned" width="80" />
      <el-table-column prop="actualHours" label="Actual" width="80" />
      <el-table-column prop="status" label="Status" width="120" />
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getActivities } from '@/apis/activity'
import { getSectionById } from '@/apis/section'
import { getStudentById } from '@/apis/student'
import { getActivityWeekOptions } from '@/utils/sectionWeeks'

const route = useRoute()
const studentId = route.params.studentId
const studentName = ref('')
const activities = ref([])
const loading = ref(false)
const selectedWeek = ref(Number(route.query.week) || 1)
const availableWeeks = ref([1])

onMounted(async () => {
  try {
    const response = await getStudentById(studentId)
    const student = response.data || {}
    studentName.value = `${student.firstName || ''} ${student.lastName || ''}`.trim()
    if (student.sectionId) {
      const sectionResponse = await getSectionById(student.sectionId)
      availableWeeks.value = getActivityWeekOptions(sectionResponse.data || {})
      if (!availableWeeks.value.includes(selectedWeek.value)) {
        selectedWeek.value = availableWeeks.value[0]
      }
    }
  } catch {}

  await load()
})

async function load() {
  loading.value = true
  try {
    const response = await getActivities({ studentId, week: selectedWeek.value })
    activities.value = response.data || []
  } catch {
    activities.value = []
  } finally {
    loading.value = false
  }
}
</script>
