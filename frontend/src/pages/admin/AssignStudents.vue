<template>
  <div class="page-container" v-loading="loading">
    <h1 class="page-title">Assign Students to Teams</h1>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8">
        <el-card><h3 style="margin-bottom:12px">Unassigned Students</h3>
          <div v-for="s in unassigned" :key="s.id" style="display:flex;justify-content:space-between;align-items:center;padding:8px 0;border-bottom:1px solid #eee">
            <span>{{ s.firstName }} {{ s.lastName }}</span>
            <el-select v-model="assignments[s.id]" placeholder="Assign to..." size="small" style="width:140px" @change="handleAssign(s.id)">
              <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
            </el-select>
          </div>
          <el-empty v-if="!unassigned.length" description="All students assigned" />
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card v-for="t in teams" :key="t.id" style="margin-bottom:12px">
          <h3>{{ t.name }}</h3>
          <el-tag v-for="s in t.students" :key="s.id" closable @close="handleRemove(t.id, s.id)" style="margin:4px">{{ s.firstName }} {{ s.lastName }}</el-tag>
          <el-empty v-if="!t.students?.length" description="No students" :image-size="40" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSectionById } from '@/apis/section'
import { assignStudentToTeam, removeStudentFromTeam } from '@/apis/team'

const route = useRoute()
const id = route.params.id
const teams = ref([])
const unassigned = ref([])
const loading = ref(false)
const assignments = reactive({})

async function load() {
  loading.value = true
  try {
    const res = await getSectionById(id)
    teams.value = res.data?.teams || []
    unassigned.value = res.data?.unassignedStudents || []
  } catch {} finally { loading.value = false }
}
onMounted(load)

async function handleAssign(studentId) {
  const teamId = assignments[studentId]
  if (!teamId) return
  try { await assignStudentToTeam(teamId, studentId); ElMessage.success('Student assigned'); load() } catch {}
}

async function handleRemove(teamId, studentId) {
  try { await removeStudentFromTeam(teamId, studentId); ElMessage.success('Student removed'); load() } catch {}
}
</script>
