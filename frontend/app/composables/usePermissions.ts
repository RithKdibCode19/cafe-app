export const usePermissions = () => {
    const { user } = useAuth()

    const permissions = computed(() => {
        return user.value?.permissions || []
    })

    const role = computed(() => {
        return user.value?.roleName || 'GUEST'
    })

    const hasPermission = (code: string) => {
        if (isSuperAdmin.value) return true
        return permissions.value.includes(code)
    }

    const hasAnyPermission = (codes: string[]) => {
        if (isSuperAdmin.value) return true
        return codes.some(code => permissions.value.includes(code))
    }

    const isSuperAdmin = computed(() => {
        return role.value === 'ADMIN' || permissions.value.includes('SYS_ALL')
    })

    const isManager = computed(() => {
        return role.value === 'MANAGER'
    })

    /**
     * Permission Groups
     */
    const canAccessPOS = computed(() => hasAnyPermission(['POS_VIEW', 'POS_ORDER']))
    const canAccessInventory = computed(() => hasAnyPermission(['INV_VIEW', 'INV_ADJUST', 'INV_TRANSFER']))
    const canAccessEmployees = computed(() => hasAnyPermission(['EMP_VIEW', 'EMP_MANAGE']))
    const canAccessMenu = computed(() => hasAnyPermission(['MENU_VIEW', 'MENU_MANAGE']))
    const canAccessReports = computed(() => hasAnyPermission(['RPT_DAILY', 'RPT_INV']))

    return {
        permissions,
        role,
        hasPermission,
        hasAnyPermission,
        isSuperAdmin,
        isManager,
        canAccessPOS,
        canAccessInventory,
        canAccessEmployees,
        canAccessMenu,
        canAccessReports
    }
}
