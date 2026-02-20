// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  // Modules
  modules: ["@nuxtjs/tailwindcss", "@vueuse/nuxt", "@nuxtjs/google-fonts", "@nuxtjs/i18n"],

  i18n: {
    locales: [
      { code: 'en', iso: 'en-US', file: 'en.json', name: 'English' },
      { code: 'km', iso: 'km-KH', file: 'km.json', name: 'Khmer' }
    ],
    defaultLocale: 'en',
    strategy: 'no_prefix', // Start simple
    langDir: 'locales',
    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'i18n_redirected',
      redirectOn: 'root',  // recommended
    }
  },

  // CSS
  css: [],

  // Google Fonts configuration
  googleFonts: {
    families: {
      'Plus Jakarta Sans': [300, 400, 500, 600, 700, 800],
      Outfit: [300, 400, 500, 600, 700, 800],
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
    // These values are only available on the server
    apiBase: process.env.NUXT_API_BASE_INTERNAL || 'http://localhost:8081',

    public: {
      // These values are available on both server and client
      apiBase: process.env.NUXT_PUBLIC_API_BASE || '/api'
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
      title: 'Cofeoshop',
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
