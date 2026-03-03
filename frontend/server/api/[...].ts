import { proxyRequest } from 'h3'

export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  // apiBaseInternal is mapped to NUXT_API_BASE_INTERNAL
  const target = config.apiBaseInternal
  
  // path includes /api (e.g. /api/auth/login)
  const path = event.path.replace(/^\/api/, '')
  const fullTargetUrl = `${target}${path}`
  
  console.log(`[Proxy] ${event.method} ${event.path} -> ${fullTargetUrl}`)
  
  return proxyRequest(event, fullTargetUrl, {
    fetchOptions: {
      headers: {
        ...getHeaders(event),
        host: undefined,
        connection: undefined
      }
    }
  })
})
