import request from '@/utils/request'

export function getSections(params) {
  return request.get('/sections', { params })
}

export function getSectionById(id) {
  return request.get(`/sections/${id}`)
}

export function createSection(data) {
  return request.post('/sections', data)
}

export function updateSection(id, data) {
  return request.put(`/sections/${id}`, data)
}

export function updateActiveWeeks(id, activeWeeks) {
  return request.put(`/sections/${id}/active-weeks`, { activeWeeks })
}

export function deleteSection(id) {
  return request.delete(`/sections/${id}`)
}
