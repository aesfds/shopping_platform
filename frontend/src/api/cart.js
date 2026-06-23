import { request } from './http'

export function getCartItems() {
  return request('/api/cart')
}

export function addCartItem(product, quantity = 1) {
  const productId = typeof product === 'object' ? product.id : product
  return request('/api/cart/items', {
    method: 'POST',
    body: JSON.stringify({ productId, quantity })
  })
}

export function updateCartItem(productId, patch) {
  return request(`/api/cart/items/${productId}`, {
    method: 'PUT',
    body: JSON.stringify(patch)
  })
}

export function removeCartItem(productId) {
  return request(`/api/cart/items/${productId}`, {
    method: 'DELETE'
  })
}

export function removeSelectedCartItems() {
  return request('/api/cart/items/selected', {
    method: 'DELETE'
  })
}
