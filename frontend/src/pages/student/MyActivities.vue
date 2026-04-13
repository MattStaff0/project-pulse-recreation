<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">My Weekly Activities</h1>
      <el-button type="primary" @click="showAddDialog = true">Add Activity</el-button>
    </div>

    <el-select v-model="selectedWeek" placeholder="Select Week" style="margin-bottom:16px;width:200px" @change="loadActivities">
      <el-option v-for="w in 15" :key="w" :label="`Week ${w}`" :value="w" />
    </el-select>

    <el-table :data="activities" stripe v-loading="loading">
      <el-table-column prop="category" label="Category" width="140">
        <template #default="{ row }"><el-tag>{{ row.category }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="activity" label="Activity" />
      <el-table-column prop="description" label="Description" show-overflow-tooltip />
      <el-table-column prop="plannedHours" label="Planned" width="80" />
      <el-table-column prop="actualHours" label="Actual" width="80" />
      <el-table-column prop="status" label="Status" width="130">
        <template #default="{ row }">
          <el-tag :type="row.status === 'DONE' ? 'success' : row.status === 'UNDER_TESTING' ? 'warning' : 'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="150">
        <template #default="{ row }">
          <el-button type="primary" link @click="editActivity(row)">Edit</el-button>
          <el-popconfirm title="Delete?" @confirm="handleDelete(row.id)">
            <template #reference><el-button type="danger" link>Delete</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="showAddDialog" :title="editingId ? 'Edit Activity' : 'Add Activity'" width="550px">
      <el-form :model="actForm" label-position="top">
        <el-form-item label="Category">
          <el-select v-model="actForm.category" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="Activity"><el-input v-model="actForm.activity" /></el-form-item>
        <el-form-item label="Description"><el-input v-model="actForm.description" type="textarea" /></el-form-item>
        <el-row :gutter="12">
          <el-col :span="12"><el-form-item label="Planned Hours"><el-input-number v-model="actForm.plannedHours" :min="0" :step="0.5" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="Actual Hours"><el-input-number v-model="actForm.actualHours" :min="0" :step="0.5" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="Status">
          <el-select v-model="actForm.status" style="width:100%">
            <el-option label="In Progress" value="IN_PROGRESS" />
            <el-option label="Under Testing" value="UNDER_TESTING" />
            <el-option label="Done" value="DONE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">Cancel</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">{{ editingId ? 'Save' : 'Add' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '@/stores/userInfo'
import { getActivities, createActivity, updateActivity, deleteActivity } from '@/apis/activity'

const userInfoStore = useUserInfoStore()
const activities = ref([])
const loading = ref(false)
const saving = ref(false)
const selectedWeek = ref(1)
const showAddDialog = ref(false)
const editingId = ref(null)
const categories = ['DEVELOPMENT', 'TESTING', 'BUGFIX', 'COMMUNICATION', 'DOCUMENTATION', 'DESIGN', 'PLANNING', 'LEARNING', 'DEPLOYMENT', 'SUPPORT', 'MISCELLANEOUS']
const actForm = ref({ category: 'DEVELOPMENT', activity: '', description: '', plannedHours: 0, actualHours: 0, status: 'IN_PROGRESS' })

async function loadActivities() {
  loading.value = true
  try {
    const res = await getActivities({ studentId: userInfoStore.userInfo.id, week: selectedWeek.value })
    activities.value = res.data || []
  } catch {} finally { loading.value = false }
}

onMounted(loadActivities)

function editActivity(row) {
  editingId.value = row.id
  actForm.value = { category: row.category, activity: row.activity, description: row.description, plannedHours: row.plannedHours, actualHours: row.actualHours, status: row.status }
  showAddDialog.value = true
}

async function handleSave() {
  saving.value = true
  try {
    if (editingId.value) {
      await updateActivity(editingId.value, actForm.value)
      ElMessage.success('Activity updated')
    } else {
      await createActivity({ ...actForm.value, studentId: userInfoStore.userInfo.id, week: selectedWeek.value })
      ElMessage.success('Activity added')
    }
    showAddDialog.value = false
    editingId.value = null
    actForm.value = { category: 'DEVELOPMENT', activity: '', description: '', plannedHours: 0, actualHours: 0, status: 'IN_PROGRESS' }
    loadActivities()
  } catch {} finally { saving.value = false }
}

async function handleDelete(id) {
  try { await deleteActivity(id); ElMessage.success('Activity deleted'); loadActivities() } catch {}
}
</script>
