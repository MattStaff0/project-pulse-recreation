<template>
  <div class="page-container">
    <h1 class="page-title">Section Peer Evaluation Report</h1>
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <el-select v-model="selectedSection" placeholder="Section" style="width:100%" @change="loadReport">
          <el-option v-for="s in sections" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-select v-model="selectedWeek" placeholder="Week" style="width:100%" @change="loadReport">
          <el-option v-for="w in 15" :key="w" :label="`Week ${w}`" :value="w" />
        </el-select>
      </el-col>
    </el-row>

    <el-table :data="reports" stripe v-loading="loading">
      <el-table-column prop="studentName" label="Student" />
      <el-table-column v-for="(score, criterion) in (reports[0]?.averageScores || {})" :key="criterion" :label="criterion" width="120">
        <template #default="{ row }">{{ row.averageScores?.[criterion] != null ? row.averageScores[criterion].toFixed(1) : '-' }}</template>
      </el-table-column>
      <el-table-column label="Avg Total" width="100">
        <template #default="{ row }"><strong>{{ row.averageTotal != null ? row.averageTotal.toFixed(1) : '-' }}</strong></template>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSections } from '@/apis/section'
import { getSectionReport } from '@/apis/evaluation'
import { getStudents } from '@/apis/student'

const router = useRouter()
const sections = ref([])
const reports = ref([])
const loading = ref(false)
const selectedSection = ref(null)
const selectedWeek = ref(1)

onMounted(async () => {
  try {
    const res = await getSections()
    sections.value = res.data || []
    if (sections.value.length) { selectedSection.value = sections.value[0].id; loadReport() }
  } catch {}
})

async function loadReport() {
  if (!selectedSection.value) return
  loading.value = true
  try {
    const res = await getSectionReport(selectedSection.value, selectedWeek.value)
    reports.value = res.data || []
  } catch {} finally { loading.value = false }
}

function viewStudentReport(row) {
  // Find student id from students list
  const students = ref([])
  getStudents({ sectionId: selectedSection.value }).then(res => {
    const s = (res.data || []).find(st => `${st.firstName} ${st.lastName}` === row.studentName)
    if (s) router.push(`/student-eval-report/${s.id}?week=${selectedWeek.value}`)
  })
}
</script>
