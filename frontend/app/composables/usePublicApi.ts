/**
 * Composable for public API calls (no auth required).
 * Used by the QR web menu for browsing and ordering.
 */
export const usePublicApi = () => {
    const config = useRuntimeConfig()

    const fetchPublic = <T>(endpoint: string, options?: Parameters<typeof $fetch>[1]) => {
        const baseURL = import.meta.server ? config.apiBase : config.public.apiBase

        return $fetch<T>(`${baseURL}${endpoint}`, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options?.headers
            }
        })
    }

    const get = <T>(endpoint: string, params?: Record<string, any>) => {
        return fetchPublic<T>(endpoint, { method: 'GET', params })
    }

    const post = <T>(endpoint: string, body?: any) => {
        return fetchPublic<T>(endpoint, { method: 'POST', body })
    }

    return { fetchPublic, get, post }
}
