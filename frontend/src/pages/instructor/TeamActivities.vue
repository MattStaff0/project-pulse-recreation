<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Team Activities - {{ teamName }}</h1>
      <el-button @click="$router.back()">Back</el-button>
    </div>
    <el-select v-model="selectedWeek" placeholder="Week" style="margin-bottom:16px;width:200px" @change="load">
      <el-option v-for="w in 15" :key="w" :label="`Week ${w}`" :value="w" />
    </el-select>

    <el-table :data="activities" stripe v-loading="loading">
      <el-table-column prop="studentName" label="Student" />
      <el-table-column prop="category" label="Category" width="140"><template #default="{ row }"><el-tag>{{ row.category }}</el-tag></template></el-table-column>
      <el-table-column prop="activity" label="Activity" />
      <el-table-column prop="description" label="Description" show-overflow-tooltip />
      <el-table-column prop="plannedHours" label="Planned" width="80" />
      <el-table-column prop="actualHours" label="Actual" width="80" />
      <el-table-column prop="status" label="Status" width="120">
        <template #default="{ row }"><el-tag :type="row.status === 'DONE' ? 'success' : 'info'">{{ row.status }}</el-tag></template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getActivities } from '@/apis/activity'
import { getTeamById } from '@/apis/team'

const route = useRoute()
const teamId = route.params.teamId
const teamName = ref('')
const activities = ref([])
const loading = ref(false)
const selectedWeek = ref(Number(route.query.week) || 1)

async function load() {
  loading.value = true
  try { const res = await getActivities({ teamId, week: selectedWeek.value }); activities.value = res.data || [] } catch {} finally { loading.value = false }
}

onMounted(async () => {
  try { const res = await getTeamById(teamId); teamName.value = res.data?.name || '' } catch {}
  load()
})
</script>
