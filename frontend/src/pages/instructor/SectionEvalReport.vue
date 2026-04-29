<template>
  <div class="page-container">
    <h1 class="page-title">Section Peer Evaluation Report</h1>

    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <el-select v-model="selectedSection" placeholder="Section" style="width:100%" @change="handleSectionChange">
          <el-option v-for="section in sections" :key="section.id" :label="section.name" :value="section.id" />
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-select v-model="selectedWeek" placeholder="Week" style="width:100%" @change="loadReport">
          <el-option v-for="week in availableWeeks" :key="week" :label="`Week ${week}`" :value="week" />
        </el-select>
      </el-col>
    </el-row>

    <el-alert
      v-if="missingEvaluators.length"
      type="warning"
      :closable="false"
      style="margin-bottom:16px"
      show-icon
      :title="`Missing peer evaluations for Week ${selectedWeek}`"
      :description="missingEvaluators.map(student => `${student.name}${student.teamName ? ` (${student.teamName})` : ''}`).join(', ')"
    />

    <el-table :data="reportRows" stripe v-loading="loading">
      <el-table-column prop="studentName" label="Student" />
      <el-table-column prop="teamName" label="Team" width="180" />
      <el-table-column v-for="criterion in criteria" :key="criterion" :label="criterion" width="120">
        <template #default="{ row }">
          {{ row.averageScores?.[criterion] != null ? row.averageScores[criterion].toFixed(1) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="Avg Total" width="100">
        <template #default="{ row }">
          <strong>{{ row.averageTotal != null ? row.averageTotal.toFixed(1) : '-' }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="140">
        <template #default="{ row }">
          <el-button type="primary" link @click="viewStudentReport(row)">Details</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getSectionReport } from '@/apis/evaluation'
import { getSectionById, getSections } from '@/apis/section'
import { getEvaluationWeekOptions } from '@/utils/sectionWeeks'

const router = useRouter()
const sections = ref([])
const reportRows = ref([])
const missingEvaluators = ref([])
const loading = ref(false)
const selectedSection = ref(null)
const selectedWeek = ref(null)
const availableWeeks = ref([1])

const criteria = computed(() => Object.keys(reportRows.value[0]?.averageScores || {}))

onMounted(async () => {
  try {
    const response = await getSections()
    sections.value = response.data || []
    if (sections.value.length) {
      selectedSection.value = sections.value[0].id
      await handleSectionChange(selectedSection.value)
    }
  } catch {}
})

async function handleSectionChange(sectionId) {
  if (!sectionId) return

  try {
    const response = await getSectionById(sectionId)
    const section = response.data || {}
    const weekOptions = getEvaluationWeekOptions(section)
    availableWeeks.value = weekOptions.activeWeeks
    selectedWeek.value = availableWeeks.value.includes(selectedWeek.value)
      ? selectedWeek.value
      : weekOptions.defaultWeek
    await loadReport()
  } catch {
    availableWeeks.value = [1]
    selectedWeek.value = 1
    reportRows.value = []
    missingEvaluators.value = []
  }
}

async function loadReport() {
  if (!selectedSection.value || selectedWeek.value == null) return

  loading.value = true
  try {
    const response = await getSectionReport(selectedSection.value, selectedWeek.value)
    reportRows.value = response.data?.students || []
    missingEvaluators.value = response.data?.missingEvaluators || []
  } catch {
    reportRows.value = []
    missingEvaluators.value = []
  } finally {
    loading.value = false
  }
}

function viewStudentReport(row) {
  router.push(`/student-eval-report/${row.studentId}?week=${selectedWeek.value}`)
}
</script>
