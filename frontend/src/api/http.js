const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

export async function request(path, options = {}) {
  const token = typeof window === 'undefined' ? '' : sessionStorage.getItem('token')
  const isFormData = typeof FormData !== 'undefined' && options.body instanceof FormData
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    }
  })

  const body = await response.json().catch(() => null)

  if (!response.ok) {
    if (response.status === 401) {
      if (typeof window !== 'undefined') {
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('user')
        sessionStorage.removeItem('adminToken')
        sessionStorage.removeItem('adminUser')
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        localStorage.removeItem('adminToken')
        localStorage.removeItem('adminUser')
        window.dispatchEvent(new Event('auth:changed'))
        window.dispatchEvent(new Event('admin:changed'))
      }
    }
    throw new Error(body?.message || `Request failed with status ${response.status}`)
  }

  if (body.code !== 0) {
    throw new Error(body.message || 'Request failed')
  }

  return body.data
}
