<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;flex-direction:column">
    <h1 style="font-size:72px;color:#f56c6c">403</h1>
    <p style="font-size:18px;color:#606266">You do not have permission to access this page.</p>
    <el-button type="primary" @click="$router.push('/')" style="margin-top:20px">Go Home</el-button>
  </div>
</template>
