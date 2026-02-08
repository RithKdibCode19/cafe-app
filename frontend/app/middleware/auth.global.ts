export default defineNuxtRouteMiddleware((to, from) => {
    const { isLoggedIn } = useAuth()

    // If not logged in and not on login page, redirect to login
    if (!isLoggedIn.value && to.path !== '/login') {
        return navigateTo('/login')
    }

    // If logged in and on login page, redirect to home or dashboard
    if (isLoggedIn.value && to.path === '/login') {
        return navigateTo('/')
    }
})
