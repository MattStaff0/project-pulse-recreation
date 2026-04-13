import request from '@/utils/request'

export function getRubrics(params) {
  return request.get('/rubrics', { params })
}

export function getRubricById(id) {
  return request.get(`/rubrics/${id}`)
}

export function createRubric(data) {
  return request.post('/rubrics', data)
}

export function updateRubric(id, data) {
  return request.put(`/rubrics/${id}`, data)
}

export function deleteRubric(id) {
  return request.delete(`/rubrics/${id}`)
}
