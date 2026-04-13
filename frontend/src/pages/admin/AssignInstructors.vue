<template>
  <div class="page-container" v-loading="loading">
    <h1 class="page-title">Assign Instructors to Teams</h1>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8">
        <el-card><h3 style="margin-bottom:12px">Available Instructors</h3>
          <div v-for="i in instructors" :key="i.id" style="display:flex;justify-content:space-between;align-items:center;padding:8px 0;border-bottom:1px solid #eee">
            <span>{{ i.firstName }} {{ i.lastName }}</span>
            <el-select v-model="assignments[i.id]" placeholder="Assign to..." size="small" style="width:140px" @change="handleAssign(i.id)">
              <el-option v-for="t in teams" :key="t.id" :label="t.name" :value="t.id" />
            </el-select>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card v-for="t in teams" :key="t.id" style="margin-bottom:12px">
          <h3>{{ t.name }}</h3>
          <el-tag v-for="i in t.instructors" :key="i.id" closable @close="handleRemove(t.id, i.id)" type="success" style="margin:4px">{{ i.firstName }} {{ i.lastName }}</el-tag>
          <el-empty v-if="!t.instructors?.length" description="No instructors" :image-size="40" />
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
import { getInstructors } from '@/apis/instructor'
import { assignInstructorToTeam, removeInstructorFromTeam } from '@/apis/team'

const route = useRoute()
const id = route.params.id
const teams = ref([])
const instructors = ref([])
const loading = ref(false)
const assignments = reactive({})

async function load() {
  loading.value = true
  try {
    const [secRes, insRes] = await Promise.all([getSectionById(id), getInstructors()])
    teams.value = secRes.data?.teams || []
    instructors.value = (insRes.data || []).filter(i => i.enabled)
  } catch {} finally { loading.value = false }
}
onMounted(load)

async function handleAssign(instructorId) {
  const teamId = assignments[instructorId]
  if (!teamId) return
  try { await assignInstructorToTeam(teamId, instructorId); ElMessage.success('Instructor assigned'); load() } catch {}
}

async function handleRemove(teamId, instructorId) {
  try { await removeInstructorFromTeam(teamId, instructorId); ElMessage.success('Instructor removed'); load() } catch {}
}
</script>
