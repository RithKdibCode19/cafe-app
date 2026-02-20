/**
 * Customer-facing cart for QR web menu.
 * Separate from the POS useCart composable.
 */

export interface MenuCartItem {
    menuItemId: number
    name: string
    price: number
    qty: number
    imageUrl?: string
    note?: string
    variantId?: number
    variantSize?: string
    variantPrice?: number
    addOns?: { addOnId: number; name: string; price: number }[]
    addOnTotal: number
}

export const useMenuCart = () => {
    const items = useState<MenuCartItem[]>('menuCartItems', () => [])
    const tableNo = useState<string>('menuCartTable', () => '')
    const branchCode = useState<string>('menuCartBranch', () => '')

    const addItem = (item: Omit<MenuCartItem, 'qty' | 'addOnTotal'> & { qty?: number; addOnTotal?: number }) => {
        // Check if same item with same variant and no add-ons already in cart
        const existing = items.value.find(
            i => i.menuItemId === item.menuItemId
                && i.variantId === item.variantId
                && (!i.addOns || i.addOns.length === 0)
                && (!item.addOns || item.addOns.length === 0)
        )

        if (existing) {
            existing.qty += (item.qty || 1)
        } else {
            items.value.push({
                ...item,
                qty: item.qty || 1,
                addOnTotal: item.addOns?.reduce((sum, a) => sum + a.price, 0) || 0
            })
        }
    }

    const removeItem = (index: number) => {
        items.value.splice(index, 1)
    }

    const updateQty = (index: number, delta: number) => {
        const item = items.value[index]
        if (item) {
            item.qty += delta
            if (item.qty <= 0) {
                removeItem(index)
            }
        }
    }

    const clearCart = () => {
        items.value = []
    }

    const itemCount = computed(() => items.value.reduce((sum, item) => sum + item.qty, 0))

    const subtotal = computed(() =>
        items.value.reduce((sum, item) => {
            const price = item.variantPrice || item.price
            return sum + (price + item.addOnTotal) * item.qty
        }, 0)
    )

    const total = computed(() => subtotal.value) // No tax for customer display

    return {
        items,
        tableNo,
        branchCode,
        addItem,
        removeItem,
        updateQty,
        clearCart,
        itemCount,
        subtotal,
        total
    }
}
