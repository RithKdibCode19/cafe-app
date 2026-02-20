/**
 * Interface for Customer entity
 */
export interface Customer {
    customerId: number;
    fullName: string;
    phone?: string;
    email?: string;
    gender?: 'MALE' | 'FEMALE' | 'OTHER';
    dob?: string;
    loyaltyPoints: number;
    membershipLevel: 'BRONZE' | 'SILVER' | 'GOLD';
    status: 'ACTIVE' | 'INACTIVE';
}

/**
 * Interface for Shift entity
 */
export interface Shift {
    shiftId: number;
    userId: number;
    checkInTime: string;
    checkOutTime?: string;
    startingCash: number;
    endingCash?: number;
    status: 'OPEN' | 'CLOSED';
}

/**
 * Interface for Shift Summary
 */
export interface ShiftSummary {
    checkInTime: string;
    startingCash: number;
    expectedCash: number;
    salesByMethod: Record<string, number>;
}

/**
 * Interface for Menu Item
 */
export interface MenuItem {
    menuItemId: number;
    name: string;
    description?: string;
    basePrice: number;
    imageUrl?: string;
    status: 'AVAILABLE' | 'UNAVAILABLE';
}

/**
 * Interface for AddOn
 */
export interface AddOn {
    addonId: number;
    name: string;
    price: number;
}
