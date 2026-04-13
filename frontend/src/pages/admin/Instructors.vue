<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">Instructors</h1>
      <el-button type="primary" @click="$router.push('/invite-instructors')">Invite Instructors</el-button>
    </div>
    <el-input v-model="search" placeholder="Search..." style="max-width:300px;margin-bottom:16px" clearable />
    <el-table :data="filteredInstructors" stripe v-loading="loading">
      <el-table-column prop="firstName" label="First Name" sortable />
      <el-table-column prop="lastName" label="Last Name" sortable />
      <el-table-column prop="email" label="Email" />
      <el-table-column label="Status" width="120">
        <template #default="{ row }"><el-tag :type="row.enabled ? 'success' : 'danger'">{{ row.enabled ? 'Active' : 'Deactivated' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="Teams">
        <template #default="{ row }">
          <el-tag v-for="t in row.teams" :key="t.id" style="margin:2px">{{ t.name }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="240">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/instructors/${row.id}`)">View</el-button>
          <el-button type="warning" link @click="handleDeactivate(row.id)" v-if="row.enabled">Deactivate</el-button>
          <el-button type="success" link @click="handleReactivate(row.id)" v-else>Reactivate</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInstructors, deactivateInstructor, reactivateInstructor } from '@/apis/instructor'

const instructors = ref([])
const loading = ref(false)
const search = ref('')
const filteredInstructors = computed(() => {
  if (!search.value) return instructors.value
  const l = search.value.toLowerCase()
  return instructors.value.filter(i => `${i.firstName} ${i.lastName}`.toLowerCase().includes(l))
})

async function load() {
  loading.value = true
  try { const res = await getInstructors(); instructors.value = res.data || [] } catch {} finally { loading.value = false }
}
onMounted(load)

async function handleDeactivate(id) {
  try { await deactivateInstructor(id); ElMessage.success('Instructor deactivated'); load() } catch {}
}
async function handleReactivate(id) {
  try { await reactivateInstructor(id); ElMessage.success('Instructor reactivated'); load() } catch {}
}
</script>
