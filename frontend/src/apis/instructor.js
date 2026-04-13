import request from '@/utils/request'

export function getInstructors(params) {
  return request.get('/instructors', { params })
}

export function getInstructorById(id) {
  return request.get(`/instructors/${id}`)
}

export function inviteInstructors(data) {
  return request.post('/instructors/invite', data)
}

export function deactivateInstructor(id) {
  return request.put(`/instructors/${id}/deactivate`)
}

export function reactivateInstructor(id) {
  return request.put(`/instructors/${id}/reactivate`)
}
