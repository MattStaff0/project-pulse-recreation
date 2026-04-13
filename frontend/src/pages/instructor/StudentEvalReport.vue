<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Peer Evaluation Report - {{ report?.studentName }}</h1>
      <el-button @click="$router.back()">Back</el-button>
    </div>
    <el-select v-model="selectedWeek" placeholder="Week" style="margin-bottom:16px;width:200px" @change="load">
      <el-option v-for="w in 15" :key="w" :label="`Week ${w}`" :value="w" />
    </el-select>

    <el-card v-loading="loading" style="border-radius:12px" v-if="report">
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
        <li v-for="(c, i) in report.publicComments" :key="i" style="margin:8px 0">{{ c }}</li>
      </ul>
      <el-empty v-else description="No comments" :image-size="60" />

      <h3 style="margin-top:20px">All Evaluations (detailed)</h3>
      <el-table :data="evaluations" stripe style="margin-top:12px">
        <el-table-column prop="evaluatorName" label="Evaluator" />
        <el-table-column prop="totalScore" label="Total" width="80" />
        <el-table-column prop="publicComment" label="Public Comment" show-overflow-tooltip />
        <el-table-column prop="privateComment" label="Private Comment" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStudentReport, getEvaluations } from '@/apis/evaluation'

const route = useRoute()
const studentId = route.params.studentId
const selectedWeek = ref(Number(route.query.week) || 1)
const report = ref(null)
const evaluations = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const [reportRes, evalsRes] = await Promise.all([
      getStudentReport(studentId, selectedWeek.value),
      getEvaluations({ evaluateeId: studentId, week: selectedWeek.value })
    ])
    report.value = reportRes.data || null
    evaluations.value = evalsRes.data || []
  } catch {} finally { loading.value = false }
}
onMounted(load)
</script>
