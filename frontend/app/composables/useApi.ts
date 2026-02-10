/**
 * Composable for making API calls to the backend
 */
export const useApi = () => {
    const config = useRuntimeConfig()

    const { token } = useAuth()

    /**
     * Fetch data from the API
     * @param endpoint - API endpoint (without /api prefix)
     * @param options - Fetch options
     */
    const fetchApi = <T>(endpoint: string, options?: Parameters<typeof $fetch>[1]) => {
        
        // Fix: Use any or correct HeadersInit type to avoid TS errors with Headers object
        const headers: any = {
            'Content-Type': 'application/json',
            ...options?.headers
        }

        if (token.value) {
            headers['Authorization'] = `Bearer ${token.value}`
        }

        return $fetch<T>(`${config.public.apiBase}${endpoint}`, {
            ...options,
            headers
        })
    }

    /**
     * GET request
     */
    const get = <T>(endpoint: string, params?: Record<string, any>) => {
        return fetchApi<T>(endpoint, {
            method: 'GET',
            params
        })
    }

    /**
     * POST request
     */
    const post = <T>(endpoint: string, body: Record<string, any>) => {
        return fetchApi<T>(endpoint, {
            method: 'POST',
            body
        })
    }

    /**
     * PUT request
     */
    const put = <T>(endpoint: string, body: Record<string, any>) => {
        return fetchApi<T>(endpoint, {
            method: 'PUT',
            body
        })
    }

    /**
     * DELETE request
     */
    const del = <T>(endpoint: string) => {
        return fetchApi<T>(endpoint, {
            method: 'DELETE'
        })
    }

    return {
        fetchApi,
        get,
        post,
        put,
        del
    }
}
