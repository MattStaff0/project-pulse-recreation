<template>
  <div class="page-container">
    <h1 class="page-title">Create Section</h1>
    <el-card style="max-width:600px;margin-top:20px;border-radius:12px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="Name" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="Start Date" prop="startDate"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="End Date" prop="endDate"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="Rubric">
          <el-select v-model="form.rubricId" placeholder="Select rubric" clearable style="width:100%">
            <el-option v-for="r in rubrics" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreate" :loading="saving">Create</el-button>
          <el-button @click="$router.back()">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createSection } from '@/apis/section'
import { getRubrics } from '@/apis/rubric'

const router = useRouter()
const formRef = ref(null)
const saving = ref(false)
const rubrics = ref([])
const form = ref({ name: '', startDate: '', endDate: '', rubricId: null })
const rules = { name: [{ required: true, message: 'Required', trigger: 'blur' }] }

onMounted(async () => { try { const res = await getRubrics(); rubrics.value = res.data || [] } catch {} })

async function handleCreate() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const res = await createSection(form.value)
    if (res.flag) { ElMessage.success('Section created'); router.push('/sections') }
    else ElMessage.error(res.message)
  } catch (err) { ElMessage.error(err.response?.data?.message || 'Error') } finally { saving.value = false }
}
</script>
