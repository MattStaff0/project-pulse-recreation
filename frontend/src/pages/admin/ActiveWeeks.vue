<template>
  <div class="page-container" v-loading="loading">
    <h1 class="page-title">Active Weeks - {{ section.name }}</h1>
    <p style="color:#606266;margin:12px 0">Uncheck weeks where students do NOT need to submit WARs and peer evaluations.</p>
    <el-card style="max-width:600px;margin-top:16px;border-radius:12px">
      <el-checkbox-group v-model="activeWeeks">
        <div v-for="w in totalWeeks" :key="w" style="margin-bottom:8px">
          <el-checkbox :label="w">Week {{ w }}</el-checkbox>
        </div>
      </el-checkbox-group>
      <div style="margin-top:20px">
        <el-button type="primary" @click="handleSave" :loading="saving">Save Active Weeks</el-button>
        <el-button @click="$router.back()">Cancel</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSectionById, updateActiveWeeks } from '@/apis/section'

const route = useRoute()
const router = useRouter()
const id = route.params.id
const section = ref({})
const activeWeeks = ref([])
const loading = ref(false)
const saving = ref(false)

const totalWeeks = computed(() => {
  if (!section.value.startDate || !section.value.endDate) return 15
  const start = new Date(section.value.startDate)
  const end = new Date(section.value.endDate)
  return Math.ceil((end - start) / (7 * 24 * 60 * 60 * 1000))
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await getSectionById(id)
    section.value = res.data || {}
    if (section.value.activeWeeks) activeWeeks.value = JSON.parse(section.value.activeWeeks)
  } catch {} finally { loading.value = false }
})

async function handleSave() {
  saving.value = true
  try {
    await updateActiveWeeks(id, JSON.stringify(activeWeeks.value))
    ElMessage.success('Active weeks saved')
    router.push(`/sections/${id}`)
  } catch {} finally { saving.value = false }
}
</script>
