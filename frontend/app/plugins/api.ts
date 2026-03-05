import axios from 'axios'
import type { AxiosInstance } from 'axios'

export default defineNuxtPlugin(() => {
  const api: AxiosInstance = axios.create({
    baseURL: '/',
    headers: {
      common: {
        Accept: 'application/json, text/plain, */*'
      }
    }
  })

  // 요청 인터셉터
  api.interceptors.request.use((config) => {
    return config
  })

  // 응답 인터셉터
  api.interceptors.response.use(
    (response) => response,
    (error) => {
      console.error('API Error:', error)
      return Promise.reject(error)
    }
  )

  return {
    provide: {
      api
    }
  }
})

// 타입 추론 용도
declare module '#app' {
  interface NuxtApp {
    $api: AxiosInstance
  }
}