<template>
  <div class="page-container">
    <h1 class="page-title">Create Team</h1>
    <el-card style="max-width:600px;margin-top:20px;border-radius:12px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="Section">
          <el-select v-model="form.sectionId" placeholder="Select section" style="width:100%">
            <el-option v-for="s in sections" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="Name" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="Description"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="Website URL"><el-input v-model="form.websiteUrl" /></el-form-item>
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
import { createTeam } from '@/apis/team'
import { getSections } from '@/apis/section'

const router = useRouter()
const formRef = ref(null)
const saving = ref(false)
const sections = ref([])
const form = ref({ name: '', description: '', websiteUrl: '', sectionId: null })
const rules = { name: [{ required: true, message: 'Required', trigger: 'blur' }] }

onMounted(async () => { try { const res = await getSections(); sections.value = res.data || [] } catch {} })

async function handleCreate() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    const res = await createTeam(form.value)
    if (res.flag) { ElMessage.success('Team created'); router.push('/teams') }
    else ElMessage.error(res.message)
  } catch (err) { ElMessage.error(err.response?.data?.message || 'Error') } finally { saving.value = false }
}
</script>
