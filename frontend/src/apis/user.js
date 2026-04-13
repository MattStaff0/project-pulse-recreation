import request from '@/utils/request'

export function getUserById(id) {
  return request.get(`/users/${id}`)
}

export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

export function changePassword(id, oldPassword, newPassword) {
  return request.put(`/users/${id}/password`, { oldPassword, newPassword })
}
