<template>
  <div class="page-container">
    <h1 class="page-title">Edit Team</h1>
    <el-card style="max-width:600px;margin-top:20px;border-radius:12px" v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="Name" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="Description"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="Website URL"><el-input v-model="form.websiteUrl" /></el-form-item>
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
import { getTeamById, updateTeam } from '@/apis/team'

const route = useRoute()
const router = useRouter()
const id = route.params.id
const formRef = ref(null)
const loading = ref(false)
const saving = ref(false)
const form = ref({ name: '', description: '', websiteUrl: '' })
const rules = { name: [{ required: true, message: 'Required', trigger: 'blur' }] }

onMounted(async () => {
  loading.value = true
  try { const res = await getTeamById(id); const t = res.data; form.value = { name: t.name, description: t.description, websiteUrl: t.websiteUrl } } catch {} finally { loading.value = false }
})

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try { const res = await updateTeam(id, form.value); if (res.flag) { ElMessage.success('Team updated'); router.push(`/teams/${id}`) } } catch (err) { ElMessage.error(err.response?.data?.message || 'Error') } finally { saving.value = false }
}
</script>
