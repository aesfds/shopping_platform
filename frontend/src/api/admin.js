import { request } from './http'

function adminHeaders() {
  const token = typeof window === 'undefined' ? '' : sessionStorage.getItem('adminToken')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

function withAdminAuth(options = {}) {
  return {
    ...options,
    headers: {
      ...adminHeaders(),
      ...(options.headers || {})
    }
  }
}

export function adminLogin(data) {
  return request('/api/admin/login', {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

export function adminLogout() {
  return request('/api/admin/logout', withAdminAuth({
    method: 'POST'
  }))
}

export function getAdminUsers(keyword = '') {
  const params = new URLSearchParams()
  if (keyword) {
    params.set('keyword', keyword)
  }
  return request(`/api/admin/users?${params.toString()}`, withAdminAuth())
}

export function getAdminOrders(status = 'all', keyword = '') {
  const params = new URLSearchParams()
  params.set('status', status)
  if (keyword) {
    params.set('keyword', keyword)
  }
  return request(`/api/admin/orders?${params.toString()}`, withAdminAuth())
}

export function getAdminUserOrders(userId, status = 'all') {
  const params = new URLSearchParams()
  params.set('status', status)
  return request(`/api/admin/users/${userId}/orders?${params.toString()}`, withAdminAuth())
}

export function resetUserPassword(userId, newPassword) {
  return request(`/api/admin/users/${userId}/reset-password`, withAdminAuth({
    method: 'POST',
    body: JSON.stringify({ newPassword })
  }))
}

export function shipAdminOrder(orderId) {
  return request(`/api/admin/orders/${orderId}/ship`, withAdminAuth({
    method: 'POST'
  }))
}

export function getAdminProducts(params = {}) {
  const query = new URLSearchParams()
  query.set('status', params.status || 'all')
  if (params.keyword) {
    query.set('keyword', params.keyword)
  }
  if (params.categoryId) {
    query.set('categoryId', params.categoryId)
  }
  return request(`/api/admin/products?${query.toString()}`, withAdminAuth())
}

export function createAdminProduct(data) {
  return request('/api/admin/products', withAdminAuth({
    method: 'POST',
    body: JSON.stringify(data)
  }))
}

export function updateAdminProduct(id, data) {
  return request(`/api/admin/products/${id}`, withAdminAuth({
    method: 'PUT',
    body: JSON.stringify(data)
  }))
}

export function updateAdminProductAvailability(id, available) {
  return request(`/api/admin/products/${id}/availability`, withAdminAuth({
    method: 'PATCH',
    body: JSON.stringify({ available })
  }))
}

export function uploadAdminProductImage(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request('/api/admin/products/image', withAdminAuth({
    method: 'POST',
    body: formData
  }))
}
