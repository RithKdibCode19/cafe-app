 export default defineNuxtRouteMiddleware((to, from) => {
    const { isLoggedIn, user } = useAuth()

    // List of public paths that don't require login
    const isPublicPath = to.path === '/login' || to.path.startsWith('/menu/') || to.path === '/staff/terminal' || to.path === '/staff/scan'

    // If not logged in and not on a public path, redirect to login
    if (!isLoggedIn.value && !isPublicPath) {
        return navigateTo('/login')
    }

    // Redirect logged in users from specific landing paths to their role-based dashboard
    if (isLoggedIn.value && (to.path === '/login' || to.path === '/')) {
        const role = user.value?.roleName || ''
        
        if (role === 'ADMIN' || role === 'MANAGER') {
            return navigateTo('/admin')
        } else if (role === 'CASHIER' || role === 'BARISTA') {
            return navigateTo('/pos')
        } else if (role === 'CHEF') {
            return navigateTo('/kitchen')
        }
    }

    // Role-based access control for admin routes
    if (isLoggedIn.value && to.path.startsWith('/admin')) {
        const role = user.value?.roleName || ''
        // Cashiers and Baristas can only access /admin/orders (for void/refund with manager PIN)
        if ((role === 'CASHIER' || role === 'BARISTA') && !to.path.startsWith('/admin/orders')) {
            return navigateTo('/pos')
        }
        if (role === 'CHEF') {
            return navigateTo('/kitchen')
        }
    }
})
