import { computed, ref } from 'vue'

function readStoredAdmin() {
  if (typeof window === 'undefined') {
    return null
  }

  try {
    return JSON.parse(sessionStorage.getItem('adminUser') || 'null')
  } catch (error) {
    return null
  }
}

const adminToken = ref(typeof window === 'undefined' ? '' : sessionStorage.getItem('adminToken') || '')
const adminUser = ref(readStoredAdmin())
const isAdminLoggedIn = computed(() => Boolean(adminToken.value))

function emitAdminChanged() {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event('admin:changed'))
  }
}

function syncAdminAuth() {
  if (typeof window === 'undefined') {
    return
  }

  adminToken.value = sessionStorage.getItem('adminToken') || ''
  adminUser.value = readStoredAdmin()
}

function setAdminAuth(nextToken, nextAdmin) {
  adminToken.value = nextToken || ''
  adminUser.value = nextAdmin || null

  if (typeof window !== 'undefined') {
    if (adminToken.value) {
      sessionStorage.setItem('adminToken', adminToken.value)
    } else {
      sessionStorage.removeItem('adminToken')
    }

    if (adminUser.value) {
      sessionStorage.setItem('adminUser', JSON.stringify(adminUser.value))
    } else {
      sessionStorage.removeItem('adminUser')
    }

    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminUser')
  }

  emitAdminChanged()
}

function clearAdminAuth() {
  adminToken.value = ''
  adminUser.value = null

  if (typeof window !== 'undefined') {
    sessionStorage.removeItem('adminToken')
    sessionStorage.removeItem('adminUser')
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminUser')
  }

  emitAdminChanged()
}

if (typeof window !== 'undefined') {
  window.addEventListener('storage', syncAdminAuth)
  window.addEventListener('admin:changed', syncAdminAuth)
}

export function useAdminState() {
  return {
    adminToken,
    adminUser,
    isAdminLoggedIn,
    syncAdminAuth,
    setAdminAuth,
    clearAdminAuth
  }
}
