import { request } from './http'

export function getHomePage() {
  return request('/api/home')
}

