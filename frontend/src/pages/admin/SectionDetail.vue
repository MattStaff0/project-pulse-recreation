<template>
  <div class="page-container" v-loading="loading">
    <div class="page-header">
      <h1 class="page-title">Section: {{ section.name }}</h1>
      <div>
        <el-button @click="$router.push(`/sections/${id}/edit`)">Edit</el-button>
        <el-button @click="$router.push(`/sections/${id}/active-weeks`)">Active Weeks</el-button>
        <el-button type="primary" @click="$router.push(`/sections/${id}/invite-students`)">Invite Students</el-button>
        <el-button @click="$router.push(`/sections/${id}/assign-students`)">Assign Students</el-button>
        <el-button @click="$router.push(`/sections/${id}/assign-instructors`)">Assign Instructors</el-button>
      </div>
    </div>

    <el-descriptions :column="2" border style="margin-bottom:24px">
      <el-descriptions-item label="Name">{{ section.name }}</el-descriptions-item>
      <el-descriptions-item label="Start Date">{{ section.startDate }}</el-descriptions-item>
      <el-descriptions-item label="End Date">{{ section.endDate }}</el-descriptions-item>
      <el-descriptions-item label="Rubric">{{ section.rubric?.name || 'None' }}</el-descriptions-item>
    </el-descriptions>

    <h3 style="margin-bottom: 12px">Teams</h3>
    <el-table :data="section.teams || []" stripe style="margin-bottom:24px">
      <el-table-column prop="name" label="Team" />
      <el-table-column label="Students">
        <template #default="{ row }">
          <el-tag v-for="s in row.students" :key="s.id" style="margin:2px">{{ s.firstName }} {{ s.lastName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Instructors">
        <template #default="{ row }">
          <el-tag v-for="i in row.instructors" :key="i.id" type="success" style="margin:2px">{{ i.firstName }} {{ i.lastName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="100">
        <template #default="{ row }"><el-button type="primary" link @click="$router.push(`/teams/${row.id}`)">View</el-button></template>
      </el-table-column>
    </el-table>

    <h3 style="margin-bottom:12px">Unassigned Students</h3>
    <el-table :data="section.unassignedStudents || []" stripe>
      <el-table-column prop="firstName" label="First Name" />
      <el-table-column prop="lastName" label="Last Name" />
      <el-table-column prop="email" label="Email" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSectionById } from '@/apis/section'

const route = useRoute()
const id = route.params.id
const section = ref({})
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try { const res = await getSectionById(id); section.value = res.data || {} } catch {} finally { loading.value = false }
})
</script>
