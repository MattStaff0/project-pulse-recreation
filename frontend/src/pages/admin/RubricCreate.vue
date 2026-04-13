<template>
  <div class="page-container">
    <h1 class="page-title">Create Rubric</h1>
    <el-card style="max-width:700px;margin-top:20px;border-radius:12px">
      <el-form :model="form" ref="formRef" label-width="120px">
        <el-form-item label="Rubric Name" prop="name" :rules="[{ required: true, message: 'Required' }]">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-divider>Criteria</el-divider>
        <div v-for="(c, idx) in form.criteria" :key="idx" style="border:1px solid #eee;padding:16px;margin-bottom:12px;border-radius:8px">
          <el-row :gutter="12">
            <el-col :span="8"><el-input v-model="c.name" placeholder="Criterion name" /></el-col>
            <el-col :span="10"><el-input v-model="c.description" placeholder="Description" /></el-col>
            <el-col :span="4"><el-input-number v-model="c.maxScore" :min="1" placeholder="Max" /></el-col>
            <el-col :span="2"><el-button type="danger" :icon="'Delete'" circle @click="removeCriterion(idx)" /></el-col>
          </el-row>
        </div>
        <el-button @click="addCriterion" style="margin-bottom:20px">+ Add Criterion</el-button>
        <el-form-item>
          <el-button type="primary" @click="handleCreate" :loading="saving">Create Rubric</el-button>
          <el-button @click="$router.back()">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createRubric } from '@/apis/rubric'

const router = useRouter()
const formRef = ref(null)
const saving = ref(false)
const form = ref({
  name: '',
  criteria: [
    { name: 'Quality of work', description: 'How do you rate the quality of this teammate\'s work? (1-10)', maxScore: 10 },
    { name: 'Productivity', description: 'How productive is this teammate? (1-10)', maxScore: 10 },
    { name: 'Initiative', description: 'How proactive is this teammate? (1-10)', maxScore: 10 },
    { name: 'Courtesy', description: 'Does this teammate treat others with respect? (1-10)', maxScore: 10 },
    { name: 'Open-mindedness', description: 'How well does this teammate handle criticism? (1-10)', maxScore: 10 },
    { name: 'Engagement in meetings', description: 'How is this teammate\'s performance during meetings? (1-10)', maxScore: 10 }
  ]
})

function addCriterion() { form.value.criteria.push({ name: '', description: '', maxScore: 10 }) }
function removeCriterion(idx) { form.value.criteria.splice(idx, 1) }

async function handleCreate() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (form.value.criteria.length === 0) { ElMessage.warning('Add at least one criterion'); return }
  saving.value = true
  try {
    const res = await createRubric(form.value)
    if (res.flag) { ElMessage.success('Rubric created'); router.push('/rubrics') }
    else ElMessage.error(res.message)
  } catch (err) { ElMessage.error(err.response?.data?.message || 'Error') } finally { saving.value = false }
}
</script>
