import { request } from './http'

export function login(data) {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export function register(data) {
  return request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export function getProfile() {
  return request('/api/auth/profile')
}

export function updateProfile(data) {
  return request('/api/auth/profile', {
    method: 'PUT',
    body: JSON.stringify(data)
  })
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request('/api/auth/avatar', {
    method: 'POST',
    body: formData
  })
}

export function recharge(amount) {
  return request('/api/auth/recharge', {
    method: 'POST',
    body: JSON.stringify({ amount })
  })
}

export function logout() {
  return request('/api/auth/logout', {
    method: 'POST'
  })
}

export const authApi = {
  loginPath: '/api/auth/login',
  profilePath: '/api/auth/profile'
}
