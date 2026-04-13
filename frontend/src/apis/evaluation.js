import request from '@/utils/request'

export function getEvaluations(params) {
  return request.get('/evaluations', { params })
}

export function getEvaluationById(id) {
  return request.get(`/evaluations/${id}`)
}

export function createEvaluation(data) {
  return request.post('/evaluations', data)
}

export function updateEvaluation(id, data) {
  return request.put(`/evaluations/${id}`, data)
}

export function getStudentReport(studentId, week) {
  return request.get(`/evaluations/report/student/${studentId}`, { params: { week } })
}

export function getSectionReport(sectionId, week) {
  return request.get(`/evaluations/report/section/${sectionId}`, { params: { week } })
}
