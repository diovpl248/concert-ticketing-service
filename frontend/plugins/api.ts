import axios from 'axios'
import type { AxiosInstance } from 'axios'

export default defineNuxtPlugin((nuxtApp: any) => {
  const defaultUrl = '/'

  const api = axios.create({
    baseURL: defaultUrl,
    headers: {
      common: {
        Accept: 'application/json, text/plain, */*'
      }
    }
  })

  // 요청 인터셉터 추가
  api.interceptors.request.use((config) => {
    // 필요한 경우 토큰 등 헤더 추가 로직
    return config
  })

  // 응답 인터셉터 추가
  api.interceptors.response.use(
    (response) => {
      return response
    },
    (error) => {
      // 전역 에러 처리
      console.error('API Error:', error)
      return Promise.reject(error)
    }
  )

  return {
    provide: {
      api: api
    }
  }
})
