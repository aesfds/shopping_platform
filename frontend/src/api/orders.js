import { request } from './http'

export function getOrders(status = 'all') {
  const params = new URLSearchParams()
  params.set('status', status)
  return request(`/api/orders?${params.toString()}`)
}

export function createOrder() {
  return request('/api/orders', {
    method: 'POST'
  })
}

export function payOrder(id) {
  return request(`/api/orders/${id}/pay`, {
    method: 'POST'
  })
}

export function cancelOrder(id) {
  return request(`/api/orders/${id}/cancel`, {
    method: 'POST'
  })
}

export function completeOrder(id) {
  return request(`/api/orders/${id}/complete`, {
    method: 'POST'
  })
}
