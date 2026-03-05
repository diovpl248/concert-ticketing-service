// https://nuxt.com/docs/api/configuration/nuxt-config
import tailwindcss from "@tailwindcss/vite";

export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  modules: [
    '@pinia/nuxt'
  ],
  css: [
    './app/assets/css/main.css'
  ],
  routeRules: {
    '/api/**': { proxy: 'http://localhost:18081/api/**' },
    '/queue/**': { proxy: 'http://localhost:18080/queue/**' }
  },
  vite: {
    plugins: [
      tailwindcss(),
    ],
  },
})
