import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTokenStore = defineStore('token', () => {
  const token = ref('')

  function setToken(newToken) {
    token.value = newToken
  }

  function clearToken() {
    token.value = ''
  }

  return { token, setToken, clearToken }
}, {
  persist: true
})
