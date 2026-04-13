import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useTokenStore } from '@/stores/token'
import router from '@/router'

const baseURL = import.meta.env.VITE_SERVER_URL

const request = axios.create({
  baseURL,
  timeout: 30000
})

// Request interceptor - inject JWT
request.interceptors.request.use(
  config => {
    const tokenStore = useTokenStore()
    if (tokenStore.token) {
      config.headers.Authorization = `Bearer ${tokenStore.token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// Response interceptor - unwrap data and handle errors
let lastErrorTime = 0
request.interceptors.response.use(
  response => response.data,
  error => {
    const now = Date.now()
    const status = error.response?.status
    const message = error.response?.data?.message || error.message

    if (status === 401) {
      const tokenStore = useTokenStore()
      tokenStore.clearToken()
      router.push('/login')
      if (now - lastErrorTime > 2000) {
        ElMessage.error('Session expired. Please log in again.')
        lastErrorTime = now
      }
    } else if (status === 403) {
      router.push('/403')
    } else if (status === 404) {
      if (now - lastErrorTime > 2000) {
        ElMessage.error(message || 'Resource not found')
        lastErrorTime = now
      }
    } else {
      if (now - lastErrorTime > 2000) {
        ElMessage.error(message || 'An error occurred')
        lastErrorTime = now
      }
    }
    return Promise.reject(error)
  }
)

export default request
