import request from '@/utils/request'

export function getTeams(params) {
  return request.get('/teams', { params })
}

export function getTeamById(id) {
  return request.get(`/teams/${id}`)
}

export function createTeam(data) {
  return request.post('/teams', data)
}

export function updateTeam(id, data) {
  return request.put(`/teams/${id}`, data)
}

export function deleteTeam(id) {
  return request.delete(`/teams/${id}`)
}

export function assignStudentToTeam(teamId, studentId) {
  return request.post(`/teams/${teamId}/students/${studentId}`)
}

export function removeStudentFromTeam(teamId, studentId) {
  return request.delete(`/teams/${teamId}/students/${studentId}`)
}

export function assignInstructorToTeam(teamId, instructorId) {
  return request.post(`/teams/${teamId}/instructors/${instructorId}`)
}

export function removeInstructorFromTeam(teamId, instructorId) {
  return request.delete(`/teams/${teamId}/instructors/${instructorId}`)
}
