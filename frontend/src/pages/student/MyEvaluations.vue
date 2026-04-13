<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">My Peer Evaluations</h1>
      <el-button type="primary" @click="$router.push('/submit-evaluation')">Submit Evaluation</el-button>
    </div>
    <el-select v-model="selectedWeek" placeholder="Select Week" style="margin-bottom:16px;width:200px" @change="loadEvals">
      <el-option v-for="w in 15" :key="w" :label="`Week ${w}`" :value="w" />
    </el-select>

    <el-table :data="evaluations" stripe v-loading="loading">
      <el-table-column prop="evaluateeName" label="Evaluated" />
      <el-table-column prop="totalScore" label="Total Score" width="120" />
      <el-table-column prop="publicComment" label="Public Comment" show-overflow-tooltip />
      <el-table-column label="Ratings">
        <template #default="{ row }">
          <el-tag v-for="r in row.ratings" :key="r.id" style="margin:2px" size="small">{{ r.criterionName }}: {{ r.score }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserInfoStore } from '@/stores/userInfo'
import { getEvaluations } from '@/apis/evaluation'

const userInfoStore = useUserInfoStore()
const evaluations = ref([])
const loading = ref(false)
const selectedWeek = ref(1)

async function loadEvals() {
  loading.value = true
  try {
    const res = await getEvaluations({ evaluatorId: userInfoStore.userInfo.id, week: selectedWeek.value })
    evaluations.value = res.data || []
  } catch {} finally { loading.value = false }
}
onMounted(loadEvals)
</script>
