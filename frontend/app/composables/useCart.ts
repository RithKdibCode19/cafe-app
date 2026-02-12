import { useToast } from './useToast'

export interface AddOn {
    addonId: number
    name: string
    price: number
}

export interface CartItem {
    menuItemId: number
    name: string
    price: number
    qty: number
    imageUrl?: string
    notes?: string
    addOns?: AddOn[]
    addOnTotal?: number
}

export interface HeldOrder {
    id: string
    items: CartItem[]
    customer: { name: string; id: number | null; points: number; level: string }
    note: string
    createdAt: Date
}

export const useCart = () => {
    const cartItems = useState<CartItem[]>('cartItems', () => [])
    const heldOrders = useState<HeldOrder[]>('heldOrders', () => [])
    const customer = useState('cartCustomer', () => ({
        name: 'Walk-in Customer',
        id: null as number | null,
        points: 0,
        level: 'BRONZE'
    }))
    const pointsDiscount = useState('pointsDiscount', () => 0)
    const manualDiscount = useState('manualDiscount', () => 0)
    const toast = useToast()

    const setCustomer = (cust: any) => {
        if (!cust) {
            customer.value = { name: 'Walk-in Customer', id: null, points: 0, level: 'BRONZE' }
            pointsDiscount.value = 0
            return
        }
        customer.value = {
            name: cust.fullName || cust.name,
            id: cust.customerId || cust.id,
            points: cust.loyaltyPoints || 0,
            level: cust.membershipLevel || 'BRONZE'
        }
    }

    const redeemPoints = async (points: number) => {
        let redeemRate = 0.1
        try {
            const { get: apiGet } = useApi()
            const settings = await apiGet<any[]>('/settings')
            const rateSetting = settings?.find(s => s.key === 'LOYALTY_REDEEM_RATE')
            if (rateSetting) redeemRate = parseFloat(rateSetting.value)
        } catch (e) {
            console.error('Failed to fetch loyalty rate, using default', e)
        }

        const discount = points * redeemRate
        pointsDiscount.value = discount
        toast.info(`Redeemed ${points} points ($${discount.toFixed(2)} discount)`)
    }

    const addToCart = (product: any) => {
        const existingItem = cartItems.value.find((item) => item.menuItemId === product.menuItemId && !item.addOns?.length)

        if (existingItem) {
            existingItem.qty++
        } else {
            cartItems.value.push({
                menuItemId: product.menuItemId,
                name: product.name,
                price: product.basePrice,
                qty: 1,
                imageUrl: product.imageUrl,
                notes: '',
                addOns: [],
                addOnTotal: 0
            })
        }
        toast.success(`Added ${product.name} to cart`)
    }

    const addToCartWithAddOns = (product: any, selectedAddOns: AddOn[], notes: string = '') => {
        const addOnTotal = selectedAddOns.reduce((sum, a) => sum + a.price, 0)
        
        // Items with add-ons are always added as new line items
        cartItems.value.push({
            menuItemId: product.menuItemId,
            name: product.name,
            price: product.basePrice,
            qty: 1,
            imageUrl: product.imageUrl,
            notes,
            addOns: selectedAddOns,
            addOnTotal
        })
        
        const addOnNames = selectedAddOns.length > 0 
            ? ` with ${selectedAddOns.map(a => a.name).join(', ')}`
            : ''
        toast.success(`Added ${product.name}${addOnNames} to cart`)
    }


    const removeFromCart = (index: number) => {
        const item = cartItems.value[index]
        if (item) {
            cartItems.value.splice(index, 1)
            toast.info(`Removed ${item.name} from cart`)
        }
    }

    const updateQty = (index: number, delta: number) => {
        const item = cartItems.value[index]
        if (item) {
            item.qty += delta
            if (item.qty <= 0) {
                removeFromCart(index)
            }
        }
    }

    const clearCart = () => {
        cartItems.value = []
        pointsDiscount.value = 0
        manualDiscount.value = 0
    }

    // Hold Order Functions
    const holdOrder = (note: string = '') => {
        if (cartItems.value.length === 0) {
            toast.error('Cart is empty')
            return false
        }

        const heldOrder: HeldOrder = {
            id: `hold-${Date.now()}`,
            items: [...cartItems.value],
            customer: { ...customer.value },
            note: note || `Order #${heldOrders.value.length + 1}`,
            createdAt: new Date()
        }

        heldOrders.value.push(heldOrder)
        clearCart()
        customer.value = { name: 'Walk-in Customer', id: null, points: 0, level: 'BRONZE' }
        toast.success(`Order held: ${heldOrder.note}`)
        return true
    }

    const resumeOrder = (orderId: string) => {
        const orderIndex = heldOrders.value.findIndex(o => o.id === orderId)
        if (orderIndex === -1) {
            toast.error('Order not found')
            return false
        }

        // If current cart has items, hold it first
        if (cartItems.value.length > 0) {
            holdOrder('Auto-held order')
        }

        const order = heldOrders.value[orderIndex]
        if (!order) return false
        
        cartItems.value = [...order.items]
        customer.value = { ...order.customer }
        heldOrders.value.splice(orderIndex, 1)
        toast.success(`Resumed: ${order.note}`)
        return true
    }

    const removeHeldOrder = (orderId: string) => {
        const orderIndex = heldOrders.value.findIndex(o => o.id === orderId)
        if (orderIndex !== -1) {
            const order = heldOrders.value[orderIndex]
            if (!order) return
            heldOrders.value.splice(orderIndex, 1)
            toast.info(`Removed held order: ${order.note}`)
        }
    }


    const subtotal = computed(() => {
        return cartItems.value.reduce((sum, item) => sum + ((item.price + (item.addOnTotal || 0)) * item.qty), 0)
    })

    const tax = computed(() => {
        const afterDiscount = Math.max(0, subtotal.value - pointsDiscount.value - manualDiscount.value)
        return afterDiscount * 0.1
    })

    const total = computed(() => {
        const afterDiscount = Math.max(0, subtotal.value - pointsDiscount.value - manualDiscount.value)
        return afterDiscount + (afterDiscount * 0.1)
    })

    return {
        cartItems,
        heldOrders,
        customer,
        pointsDiscount,
        manualDiscount,
        addToCart,
        addToCartWithAddOns,
        removeFromCart,
        updateQty,
        clearCart,
        setCustomer,
        redeemPoints,
        holdOrder,
        resumeOrder,
        removeHeldOrder,
        subtotal,
        tax,
        total
    }
}

