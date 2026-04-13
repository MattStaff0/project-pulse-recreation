import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  const defaultCourseId = ref(null)
  const defaultSectionId = ref(null)

  watch(defaultCourseId, () => {
    defaultSectionId.value = null
  })

  function setDefaultCourse(id) {
    defaultCourseId.value = id
  }

  function setDefaultSection(id) {
    defaultSectionId.value = id
  }

  function clear() {
    defaultCourseId.value = null
    defaultSectionId.value = null
  }

  return { defaultCourseId, defaultSectionId, setDefaultCourse, setDefaultSection, clear }
}, {
  persist: true
})
