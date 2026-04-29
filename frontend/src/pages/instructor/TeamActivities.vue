<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ title }}</h1>
      <el-button v-if="showBackButton" @click="$router.back()">Back</el-button>
    </div>

    <el-select v-model="selectedWeek" placeholder="Week" style="margin-bottom:16px;width:220px" @change="load">
      <el-option v-for="week in availableWeeks" :key="week" :label="`Week ${week}`" :value="week" />
    </el-select>

    <el-alert
      v-if="missingStudents.length"
      type="warning"
      :closable="false"
      style="margin-bottom:16px"
      show-icon
      :title="`Missing WAR submissions for Week ${selectedWeek}`"
      :description="missingStudents.join(', ')"
    />

    <el-table :data="activities" stripe v-loading="loading">
      <el-table-column prop="studentName" label="Student" />
      <el-table-column prop="category" label="Category" width="140">
        <template #default="{ row }"><el-tag>{{ row.category }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="activity" label="Activity" />
      <el-table-column prop="description" label="Description" show-overflow-tooltip />
      <el-table-column prop="plannedHours" label="Planned" width="80" />
      <el-table-column prop="actualHours" label="Actual" width="80" />
      <el-table-column prop="status" label="Status" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'DONE' ? 'success' : 'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getActivities } from '@/apis/activity'
import { getSectionById } from '@/apis/section'
import { getTeamById } from '@/apis/team'
import { useUserInfoStore } from '@/stores/userInfo'
import { getActivityWeekOptions } from '@/utils/sectionWeeks'

const route = useRoute()
const userInfoStore = useUserInfoStore()

const resolvedTeamId = computed(() => route.params.teamId || userInfoStore.userInfo?.teamId)
const title = computed(() => userInfoStore.isStudent ? `Team WAR - ${teamName.value}` : `Team Activities - ${teamName.value}`)
const showBackButton = computed(() => !userInfoStore.isStudent)

const teamName = ref(userInfoStore.userInfo?.teamName || '')
const teamStudents = ref([])
const activities = ref([])
const missingStudents = ref([])
const loading = ref(false)
const selectedWeek = ref(Number(route.query.week) || 1)
const availableWeeks = ref([1])

onMounted(async () => {
  if (!resolvedTeamId.value) return

  try {
    const response = await getTeamById(resolvedTeamId.value)
    const team = response.data || {}
    teamName.value = team.name || teamName.value
    teamStudents.value = team.students || []

    if (team.sectionId) {
      const sectionResponse = await getSectionById(team.sectionId)
      availableWeeks.value = getActivityWeekOptions(sectionResponse.data || {})
      if (!availableWeeks.value.includes(selectedWeek.value)) {
        selectedWeek.value = availableWeeks.value[0]
      }
    }
  } catch {}

  await load()
})

async function load() {
  if (!resolvedTeamId.value) return

  loading.value = true
  try {
    const response = await getActivities({ teamId: resolvedTeamId.value, week: selectedWeek.value })
    activities.value = response.data || []
    const submittedStudentIds = new Set(activities.value.map(activity => activity.studentId))
    missingStudents.value = teamStudents.value
      .filter(student => !submittedStudentIds.has(student.id))
      .map(student => `${student.firstName} ${student.lastName}`)
  } catch {
    activities.value = []
    missingStudents.value = []
  } finally {
    loading.value = false
  }
}
</script>
