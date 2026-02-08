// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  // Modules
  modules: [
    '@nuxtjs/tailwindcss',
    '@nuxtjs/google-fonts',
    '@vueuse/nuxt'
  ],

  // CSS
  css: [],

  // Google Fonts configuration
  googleFonts: {
    families: {
      Inter: [300, 400, 500, 600, 700, 800],
      'JetBrains Mono': [400, 500, 600]
    },
    display: 'swap',
    prefetch: true,
    preconnect: true
  },

  // Tailwind configuration
  tailwindcss: {
    cssPath: '~/assets/css/main.css',
    configPath: 'tailwind.config.ts'
  },

  // Runtime config for API communication
  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || 'http://localhost:8081/api'
    }
  },

  // Development server configuration
  devServer: {
    port: 3000,
    host: '0.0.0.0'
  },

  // Nitro server configuration
  nitro: {
    // Proxy API requests to backend during development
    devProxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    },
    compressPublicAssets: true,
  },

  // App configuration
  app: {
    head: {
      title: 'Cafe POS',
      htmlAttrs: {
        lang: 'en'
      },
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { name: 'description', content: 'Cafe Point of Sale System' }
      ],
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
      ]
    }
  }
})
