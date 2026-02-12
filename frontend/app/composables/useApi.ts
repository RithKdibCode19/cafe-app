/**
 * Composable for making API calls to the backend
 */
export const useApi = () => {
    const config = useRuntimeConfig()

    /**
     * Fetch data from the API
     * @param endpoint - API endpoint (without /api prefix)
     * @param options - Fetch options
     */
    const fetchApi = <T>(endpoint: string, options?: Parameters<typeof $fetch>[1]) => {
        const { token, logout } = useAuth()
        const toast = useToast()
        
        const headers: any = {
            ...options?.headers
        }

        // Only set Content-Type if body is NOT FormData (browser sets boundary for FormData)
        if (!(options?.body instanceof FormData)) {
            headers['Content-Type'] = 'application/json'
        }

        if (token.value) {
            headers['Authorization'] = `Bearer ${token.value}`
        }

        return $fetch<T>(`${config.public.apiBase}${endpoint}`, {
            ...options,
            headers,
            onResponseError({ response }) {
                const status = response.status
                const errorData = response._data
                const message = errorData?.message || errorData?.error || 'An unexpected error occurred'

                if (status === 401) {
                    toast.error('Session expired. Please login again.')
                    logout()
                } else if (status === 403) {
                    toast.error('You do not have permission to perform this action.')
                } else if (status === 400) {
                    toast.error(message)
                } else if (status >= 500) {
                    toast.error('Server error. Please try again later.')
                } else {
                    toast.error(message)
                }
            }
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
