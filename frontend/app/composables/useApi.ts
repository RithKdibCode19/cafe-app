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

        // Use context-aware base URL
        const baseURL = process.server ? config.apiBase : config.public.apiBase

        return $fetch<T>(`${baseURL}${endpoint}`, {
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
     * Download a file from the API
     */
    const download = async (endpoint: string, filename: string) => {
        try {
            const { token } = useAuth()
            const baseURL = process.server ? config.apiBase : config.public.apiBase
            const response = await $fetch(`${baseURL}${endpoint}`, {
                method: 'GET',
                headers: {
                    'Authorization': token.value ? `Bearer ${token.value}` : ''
                },
                responseType: 'blob'
            })
            
            const url = window.URL.createObjectURL(response as Blob)
            const link = document.createElement('a')
            link.href = url
            link.setAttribute('download', filename)
            document.body.appendChild(link)
            link.click()
            link.remove()
            window.URL.revokeObjectURL(url)
        } catch (error) {
            console.error('Download failed:', error)
            const toast = useToast()
            toast.error('Failed to download file. Please try again.')
        }
    }

    return {
        fetchApi,
        get,
        post,
        put,
        del,
        download
    }
}
