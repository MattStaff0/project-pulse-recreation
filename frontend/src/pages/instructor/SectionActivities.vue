<template>
  <div class="page-container">
    <h1 class="page-title">Section Activities</h1>
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <el-select v-model="selectedSection" placeholder="Select Section" style="width:100%" @change="loadTeams">
          <el-option v-for="s in sections" :key="s.id" :label="s.name" :value="s.id" />
        </el-select>
      </el-col>
      <el-col :span="8">
        <el-select v-model="selectedWeek" placeholder="Week" style="width:100%">
          <el-option v-for="w in 15" :key="w" :label="`Week ${w}`" :value="w" />
        </el-select>
      </el-col>
    </el-row>

    <el-card v-for="team in teams" :key="team.id" style="margin-bottom:16px;border-radius:12px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <h3>{{ team.name }}</h3>
          <el-button type="primary" link @click="$router.push(`/team-activities/${team.id}?week=${selectedWeek}`)">View Team WAR</el-button>
        </div>
      </template>
      <el-table :data="team.students || []" size="small">
        <el-table-column label="Student">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push(`/student-activities/${row.id}`)">{{ row.firstName }} {{ row.lastName }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="Email" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSections, getSectionById } from '@/apis/section'

const sections = ref([])
const teams = ref([])
const selectedSection = ref(null)
const selectedWeek = ref(1)

onMounted(async () => {
  try { const res = await getSections(); sections.value = res.data || []; if (sections.value.length) { selectedSection.value = sections.value[0].id; loadTeams() } } catch {}
})

async function loadTeams() {
  if (!selectedSection.value) return
  try { const res = await getSectionById(selectedSection.value); teams.value = res.data?.teams || [] } catch {}
}
</script>
