export default defineNuxtRouteMiddleware((to, from) => {
    const { isLoggedIn } = useAuth()

    // List of public paths that don't require login
    const isPublicPath = to.path === '/login' || to.path.startsWith('/menu/')

    // If not logged in and not on a public path, redirect to login
    if (!isLoggedIn.value && !isPublicPath) {
        return navigateTo('/login')
    }

    // If logged in and on login page, redirect to home or dashboard
    if (isLoggedIn.value && to.path === '/login') {
        return navigateTo('/')
    }
})
