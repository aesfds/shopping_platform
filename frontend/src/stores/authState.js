import { computed, ref } from 'vue'

function readStoredUser() {
  if (typeof window === 'undefined') {
    return null
  }

  try {
    return JSON.parse(sessionStorage.getItem('user') || 'null')
  } catch (error) {
    return null
  }
}

const token = ref(typeof window === 'undefined' ? '' : sessionStorage.getItem('token') || '')
const user = ref(readStoredUser())
const isLoggedIn = computed(() => Boolean(token.value))

function emitAuthChanged() {
  if (typeof window === 'undefined') {
    return
  }

  window.dispatchEvent(new Event('auth:changed'))
}

function syncAuth() {
  if (typeof window === 'undefined') {
    return
  }

  token.value = sessionStorage.getItem('token') || ''
  user.value = readStoredUser()
}

function setAuth(nextToken, nextUser) {
  token.value = nextToken || ''
  user.value = nextUser || null

  if (typeof window !== 'undefined') {
    if (token.value) {
      sessionStorage.setItem('token', token.value)
    } else {
      sessionStorage.removeItem('token')
    }

    if (user.value) {
      sessionStorage.setItem('user', JSON.stringify(user.value))
    } else {
      sessionStorage.removeItem('user')
    }

    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  emitAuthChanged()
}

function setUser(nextUser) {
  user.value = nextUser || null

  if (typeof window !== 'undefined') {
    if (user.value) {
      sessionStorage.setItem('user', JSON.stringify(user.value))
    } else {
      sessionStorage.removeItem('user')
    }

    localStorage.removeItem('user')
  }

  emitAuthChanged()
}

function clearAuth() {
  token.value = ''
  user.value = null
  if (typeof window !== 'undefined') {
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('user')
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  emitAuthChanged()
}

if (typeof window !== 'undefined') {
  window.addEventListener('storage', syncAuth)
  window.addEventListener('auth:changed', syncAuth)
}

export function useAuthState() {
  return {
    token,
    user,
    isLoggedIn,
    syncAuth,
    setAuth,
    setUser,
    clearAuth
  }
}
