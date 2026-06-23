import { request } from './http'

const pageSizeDefault = 8

export function getCategories() {
  return request('/api/products/categories')
}

export function getProductList(params = {}) {
  const query = new URLSearchParams()
  query.set('keyword', params.keyword || '')
  if (params.categoryId) {
    query.set('categoryId', params.categoryId)
  }
  query.set('sort', params.sort || 'recommend')
  query.set('page', params.page || 1)
  query.set('size', params.size || pageSizeDefault)

  return request(`/api/products?${query.toString()}`)
}

export function getProductDetail(id) {
  return request(`/api/products/${id}`)
}
