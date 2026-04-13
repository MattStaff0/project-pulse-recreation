<template>
  <div class="page-container">
    <div class="page-header"><h1 class="page-title">Students</h1></div>
    <el-input v-model="search" placeholder="Search by name..." style="max-width:300px;margin-bottom:16px" clearable />
    <el-table :data="filteredStudents" stripe v-loading="loading">
      <el-table-column prop="firstName" label="First Name" sortable />
      <el-table-column prop="lastName" label="Last Name" sortable />
      <el-table-column prop="email" label="Email" />
      <el-table-column prop="teamName" label="Team" />
      <el-table-column prop="sectionName" label="Section" />
      <el-table-column label="Actions" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/students/${row.id}`)">View</el-button>
          <el-popconfirm title="Delete this student permanently?" @confirm="handleDelete(row.id)" v-if="userInfoStore.isAdmin">
            <template #reference><el-button type="danger" link>Delete</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudents, deleteStudent } from '@/apis/student'
import { useUserInfoStore } from '@/stores/userInfo'

const userInfoStore = useUserInfoStore()
const students = ref([])
const loading = ref(false)
const search = ref('')
const filteredStudents = computed(() => {
  if (!search.value) return students.value
  const l = search.value.toLowerCase()
  return students.value.filter(s => `${s.firstName} ${s.lastName}`.toLowerCase().includes(l))
})

async function load() {
  loading.value = true
  try { const res = await getStudents(); students.value = res.data || [] } catch {} finally { loading.value = false }
}
onMounted(load)

async function handleDelete(id) {
  try { await deleteStudent(id); ElMessage.success('Student deleted'); load() } catch {}
}
</script>
