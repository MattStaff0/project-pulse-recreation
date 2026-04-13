import request from '@/utils/request'

export function inviteStudents(data) {
  return request.post('/invitations/students', data)
}

export function inviteInstructors(data) {
  return request.post('/invitations/instructors', data)
}
