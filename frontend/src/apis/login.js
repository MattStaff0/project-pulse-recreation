import axios from 'axios'

const baseURL = import.meta.env.VITE_SERVER_URL

export function loginApi(email, password) {
  return axios.post(`${baseURL}/users/login`, null, {
    auth: { username: email, password }
  }).then(res => res.data)
}

export function registerApi(data) {
  return axios.post(`${baseURL}/users/register`, data).then(res => res.data)
}

export function validateInvitationApi(token) {
  return axios.get(`${baseURL}/users/validate-invitation`, { params: { token } }).then(res => res.data)
}

export function forgotPasswordApi(email) {
  return axios.post(`${baseURL}/users/forgot-password`, { email }).then(res => res.data)
}

export function resetPasswordApi(email, token, newPassword) {
  return axios.post(`${baseURL}/users/reset-password`, { email, token, newPassword }).then(res => res.data)
}
