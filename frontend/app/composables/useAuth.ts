export const useAuth = () => {
    const token = useCookie('auth_token', {
        maxAge: 60 * 60 * 24, // 1 day
        sameSite: 'lax',
    })
    
    // Store user data in a cookie as well so it persists across refreshes
    const userCookie = useCookie<any>('auth_user', {
        maxAge: 60 * 60 * 24, // 1 day
        sameSite: 'lax',
    })
    
    // Use useState but initialize from cookie
    const user = useState<any>('authUser', () => userCookie.value || null)

    const setAuth = (userData: any) => {
        user.value = userData
        userCookie.value = userData // Persist to cookie
        if (userData?.token) {
            token.value = userData.token
        }
    }

    const clearAuth = () => {
        token.value = null
        userCookie.value = null
        user.value = null
    }

    const logout = () => {
        clearAuth()
        navigateTo('/login')
    }

    // Check both token AND user data exist for valid session
    const isLoggedIn = computed(() => {
        // If token exists but no user data, this is an invalid session
        if (token.value && !user.value && !userCookie.value) {
            // Clear the orphaned token
            token.value = null
            return false
        }
        return !!token.value && !!(user.value || userCookie.value)
    })

    // Sync user from cookie if needed (for SSR/page refresh)
    if (userCookie.value && !user.value) {
        user.value = userCookie.value
    }

    return {
        token,
        user,
        setAuth,
        logout,
        clearAuth,
        isLoggedIn
    }
}
