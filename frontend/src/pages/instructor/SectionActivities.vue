<template>
  <div class="page-container">
    <h1 class="page-title">Section Activities</h1>

    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <el-select v-model="selectedSection" placeholder="Select Section" style="width:100%" @change="handleSectionChange">
          <el-option v-for="section in sections" :key="section.id" :label="section.name" :value="section.id" />
        </el-select>
      </el-col>
      <el-col :span="8">
        <el-select v-model="selectedWeek" placeholder="Week" style="width:100%" @change="loadTeams">
          <el-option v-for="week in availableWeeks" :key="week" :label="`Week ${week}`" :value="week" />
        </el-select>
      </el-col>
    </el-row>

    <el-card v-for="team in teams" :key="team.id" style="margin-bottom:16px;border-radius:12px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;gap:16px">
          <div>
            <h3 style="margin:0">{{ team.name }}</h3>
            <p style="margin:6px 0 0;color:#909399" v-if="team.missingStudents?.length">
              Missing WAR submissions: {{ team.missingStudents.join(', ') }}
            </p>
          </div>
          <el-button type="primary" link @click="$router.push(`/team-activities/${team.id}?week=${selectedWeek}`)">View Team WAR</el-button>
        </div>
      </template>

      <el-table :data="team.students || []" size="small">
        <el-table-column label="Student">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push(`/student-activities/${row.id}?week=${selectedWeek}`)">
              {{ row.firstName }} {{ row.lastName }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="Email" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getActivities } from '@/apis/activity'
import { getSectionById, getSections } from '@/apis/section'
import { getActivityWeekOptions } from '@/utils/sectionWeeks'

const sections = ref([])
const teams = ref([])
const selectedSection = ref(null)
const selectedWeek = ref(1)
const availableWeeks = ref([1])

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
    availableWeeks.value = getActivityWeekOptions(section)
    selectedWeek.value = availableWeeks.value.includes(selectedWeek.value)
      ? selectedWeek.value
      : availableWeeks.value[0]
    await loadTeams()
  } catch {
    teams.value = []
    availableWeeks.value = [1]
    selectedWeek.value = 1
  }
}

async function loadTeams() {
  if (!selectedSection.value) return

  try {
    const response = await getSectionById(selectedSection.value)
    const section = response.data || {}
    const sectionTeams = section.teams || []

    teams.value = await Promise.all(sectionTeams.map(async (team) => {
      try {
        const activitiesResponse = await getActivities({ teamId: team.id, week: selectedWeek.value })
        const submittedStudentIds = new Set((activitiesResponse.data || []).map(activity => activity.studentId))
        return {
          ...team,
          missingStudents: (team.students || [])
            .filter(student => !submittedStudentIds.has(student.id))
            .map(student => `${student.firstName} ${student.lastName}`)
        }
      } catch {
        return { ...team, missingStudents: [] }
      }
    }))
  } catch {
    teams.value = []
  }
}
</script>
