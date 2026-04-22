import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserInfoStore = defineStore('userInfo', () => {
  const userInfo = ref(null)

  const roles = computed(() => {
    if (!userInfo.value || !userInfo.value.roles) return []
    return userInfo.value.roles.toLowerCase().split(' ')
  })

  const isAdmin = computed(() => roles.value.includes('admin'))
  const isInstructor = computed(() => roles.value.includes('instructor'))
  const isStudent = computed(() => roles.value.includes('student'))

  function setUserInfo(info) {
    userInfo.value = info
  }

  function clearUserInfo() {
    userInfo.value = null
  }

  return { userInfo, roles, isAdmin, isInstructor, isStudent, setUserInfo, clearUserInfo }
}, {
  persist: true
})
