import request from '@/utils/request'

export function getStudents(params) {
  return request.get('/students', { params })
}

export function getUnassignedStudents(sectionId) {
  return request.get('/students/unassigned', { params: { sectionId } })
}

export function getStudentById(id) {
  return request.get(`/students/${id}`)
}

export function updateStudent(id, data) {
  return request.put(`/students/${id}`, data)
}

export function deleteStudent(id) {
  return request.delete(`/students/${id}`)
}
