<template>
  <div class="page-container">
    <h1 class="page-title">My Peer Evaluation Report</h1>
    <el-select v-model="selectedWeek" placeholder="Select Week" style="margin-bottom:16px;width:200px" @change="loadReport">
      <el-option v-for="w in availableWeeks" :key="w" :label="`Week ${w}`" :value="w" />
    </el-select>

    <el-card v-loading="loading" style="border-radius:12px" v-if="report">
      <h2>{{ report.studentName }} - Week {{ report.week }}</h2>
      <el-descriptions :column="1" border style="margin-top:16px">
        <el-descriptions-item v-for="(score, criterion) in report.averageScores" :key="criterion" :label="criterion">
          {{ typeof score === 'number' ? score.toFixed(1) : score }}
        </el-descriptions-item>
        <el-descriptions-item label="Average Total">
          <strong>{{ typeof report.averageTotal === 'number' ? report.averageTotal.toFixed(1) : report.averageTotal }}</strong>
        </el-descriptions-item>
      </el-descriptions>

      <h3 style="margin-top:20px">Public Comments</h3>
      <ul v-if="report.publicComments?.length">
        <li v-for="(c, i) in report.publicComments" :key="i" style="margin:8px 0;color:#606266">{{ c }}</li>
      </ul>
      <el-empty v-else description="No comments yet" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserInfoStore } from '@/stores/userInfo'
import { getStudentReport } from '@/apis/evaluation'
import { getSectionById } from '@/apis/section'
import { getEvaluationWeekOptions } from '@/utils/sectionWeeks'

const userInfoStore = useUserInfoStore()
const selectedWeek = ref(1)
const report = ref(null)
const loading = ref(false)
const availableWeeks = ref([1])

async function loadReport() {
  if (!selectedWeek.value) {
    report.value = null
    return
  }
  loading.value = true
  try {
    const res = await getStudentReport(userInfoStore.userInfo.id, selectedWeek.value)
    report.value = res.data || null
  } catch {} finally { loading.value = false }
}

onMounted(async () => {
  try {
    if (userInfoStore.userInfo?.sectionId) {
      const secRes = await getSectionById(userInfoStore.userInfo.sectionId)
      const weekOptions = getEvaluationWeekOptions(secRes.data || {})
      availableWeeks.value = weekOptions.activeWeeks
      selectedWeek.value = weekOptions.defaultWeek ?? availableWeeks.value[0] ?? 1
    }
  } catch {
    availableWeeks.value = [1]
  }
  loadReport()
})
</script>
