<template>
  <div class="page-container">
    <h1 class="page-title">Edit Section</h1>
    <el-card style="max-width:600px;margin-top:20px;border-radius:12px" v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="Name" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="Start Date"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="End Date"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="Rubric">
          <el-select v-model="form.rubricId" placeholder="Select rubric" clearable style="width:100%">
            <el-option v-for="r in rubrics" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">Save</el-button>
          <el-button @click="$router.back()">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSectionById, updateSection } from '@/apis/section'
import { getRubrics } from '@/apis/rubric'

const route = useRoute()
const router = useRouter()
const id = route.params.id
const formRef = ref(null)
const loading = ref(false)
const saving = ref(false)
const rubrics = ref([])
const form = ref({ name: '', startDate: '', endDate: '', rubricId: null })
const rules = { name: [{ required: true, message: 'Required', trigger: 'blur' }] }

onMounted(async () => {
  loading.value = true
  try {
    const [secRes, rubRes] = await Promise.all([getSectionById(id), getRubrics()])
    const s = secRes.data
    form.value = { name: s.name, startDate: s.startDate, endDate: s.endDate, rubricId: s.rubricId }
    rubrics.value = rubRes.data || []
  } catch {} finally { loading.value = false }
})

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const res = await updateSection(id, form.value)
    if (res.flag) { ElMessage.success('Section updated'); router.push(`/sections/${id}`) }
  } catch (err) { ElMessage.error(err.response?.data?.message || 'Error') } finally { saving.value = false }
}
</script>
