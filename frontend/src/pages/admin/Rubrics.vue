<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Rubrics</h1>
      <el-button type="primary" @click="$router.push('/rubrics/create')">Create Rubric</el-button>
    </div>
    <el-table :data="rubrics" stripe v-loading="loading">
      <el-table-column prop="name" label="Rubric Name" />
      <el-table-column label="Criteria">
        <template #default="{ row }">{{ row.criteria?.length || 0 }} criteria</template>
      </el-table-column>
      <el-table-column label="Actions" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="viewRubric(row)">View</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="selectedRubric?.name" width="600px">
      <el-table :data="selectedRubric?.criteria || []">
        <el-table-column prop="name" label="Criterion" />
        <el-table-column prop="description" label="Description" />
        <el-table-column prop="maxScore" label="Max Score" width="100" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRubrics } from '@/apis/rubric'

const rubrics = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const selectedRubric = ref(null)

onMounted(async () => {
  loading.value = true
  try { const res = await getRubrics(); rubrics.value = res.data || [] } catch {} finally { loading.value = false }
})

function viewRubric(rubric) { selectedRubric.value = rubric; dialogVisible.value = true }
</script>
