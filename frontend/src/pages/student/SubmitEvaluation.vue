<template>
  <div class="page-container">
    <h1 class="page-title">Submit Peer Evaluation</h1>
    <el-select v-model="selectedWeek" placeholder="Select Week" style="margin-bottom:16px;width:200px">
      <el-option v-for="w in 15" :key="w" :label="`Week ${w}`" :value="w" />
    </el-select>

    <div v-if="teammates.length === 0 && !loading">
      <el-empty description="No teammates found. You must be assigned to a team first." />
    </div>

    <el-card v-for="mate in teammates" :key="mate.id" style="margin-bottom:16px;border-radius:12px" v-loading="loading">
      <h3>{{ mate.firstName }} {{ mate.lastName }}</h3>
      <el-row :gutter="12" style="margin-top:12px">
        <el-col :span="4" v-for="c in criteria" :key="c.id">
          <div style="margin-bottom:8px;font-size:13px;color:#606266">{{ c.name }} (max {{ c.maxScore }})</div>
          <el-input-number v-model="evalData[mate.id].ratings[c.id]" :min="0" :max="c.maxScore" size="small" style="width:100%" />
        </el-col>
      </el-row>
      <el-row :gutter="12" style="margin-top:12px">
        <el-col :span="12"><el-input v-model="evalData[mate.id].publicComment" placeholder="Public comment" /></el-col>
        <el-col :span="12"><el-input v-model="evalData[mate.id].privateComment" placeholder="Private comment (instructor only)" /></el-col>
      </el-row>
    </el-card>

    <el-button type="primary" @click="handleSubmit" :loading="submitting" v-if="teammates.length > 0" style="margin-top:12px">Submit All Evaluations</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/stores/userInfo'
import { getStudents } from '@/apis/student'
import { getRubrics } from '@/apis/rubric'
import { getSections } from '@/apis/section'
import { createEvaluation } from '@/apis/evaluation'

const router = useRouter()
const userInfoStore = useUserInfoStore()
const selectedWeek = ref(1)
const teammates = ref([])
const criteria = ref([])
const loading = ref(false)
const submitting = ref(false)
const evalData = reactive({})

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    // Get teammates from same team
    const studentRes = await getStudents({ teamId: userInfoStore.userInfo.teamId || '' })
    const allStudents = studentRes.data || []
    // Include self in evaluations
    teammates.value = allStudents

    // Get rubric criteria from section
    const secRes = await getSections()
    const sections = secRes.data || []
    if (sections.length > 0 && sections[0].rubricId) {
      const rubRes = await getRubrics()
      const rubrics = rubRes.data || []
      const rubric = rubrics.find(r => r.id === sections[0].rubricId)
      if (rubric) criteria.value = rubric.criteria
    }

    // Initialize eval data for each teammate
    for (const mate of teammates.value) {
      evalData[mate.id] = { publicComment: '', privateComment: '', ratings: {} }
      for (const c of criteria.value) {
        evalData[mate.id].ratings[c.id] = 0
      }
    }
  } catch {} finally { loading.value = false }
}

async function handleSubmit() {
  submitting.value = true
  try {
    for (const mate of teammates.value) {
      const data = evalData[mate.id]
      const ratings = criteria.value.map(c => ({ criterionId: c.id, score: data.ratings[c.id] || 0 }))
      await createEvaluation({
        evaluatorId: userInfoStore.userInfo.id,
        evaluateeId: mate.id,
        week: selectedWeek.value,
        publicComment: data.publicComment,
        privateComment: data.privateComment,
        ratings
      })
    }
    ElMessage.success('All evaluations submitted!')
    router.push('/my-evaluations')
  } catch (err) {
    ElMessage.error('Error submitting evaluations')
  } finally { submitting.value = false }
}
</script>
