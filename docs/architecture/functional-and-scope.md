# Functional Scope & Requirements

This document outlines the functional requirements of the Cafe POS system, aligned with the database schema `docs/architecture/database-schema.md`.

## 1. POS Module (Point of Sale)

**Mapped Entities**: `Order`, `OrderItem`, `Payment`, `Customer`

*   **Order Processing**:
    *   **Creation**: Intuitive interface to select items, variants (size), and add-ons (toppings).
    *   **Calculations**: Auto-calculate subtotal, discounts, tax, and grand total.
    *   **Order Types**: Support for Dine-in, Takeaway, and Delivery.
*   **Payment & Transactions**:
    *   **Methods**: Support Cash, Credit/Debit Card, and QR payments.
    *   **Split Payments**: Ability to split bills (optional future scope).
    *   **Receipts**: Generate thermal prints or digital receipts (email).
    *   **Refunds/Voids**: Strict workflow requiring approval, reason logging, and audit trails.
*   **Daily Operations**:
    *   **Shift Handover**: Summary of cash in drawer vs. expected sales.
    *   **Closing Reports**: Daily sales summary by cashier and payment method.

## 2. Menu Management

**Mapped Entities**: `Category`, `MenuItem`, `Variant`, `AddOn`

*   **Catalog Management**:
    *   **CRUD**: Create, read, update, delete for Categories and Menu Items.
    *   **Rich Media**: Support for item images (`image_url`).
    *   **Organization**: Group items by categories (e.g., Coffee, Pastry).
*   **Customization**:
    *   **Variants**: Manage size-based pricing (e.g., Small, Medium, Large) via `VariantEntity`.
    *   **Add-ons**: Global or item-specific add-ons (e.g., Extra Shot, Sugar Level) via `AddOnEntity`.
*   **Promotions** (Future Scope):
    *   Happy hours and combo sets.

## 3. Inventory & Supply Chain

**Mapped Entities**: `Supplier`, `StockIn`, `Ingredient`

*   **Supply Management**:
    *   **Suppliers**: Manage vendor profiles (`SupplierEntity`) with contact info and status.
    *   **Purchasing**: Record stock-in transactions (`StockInEntity`) with invoice numbers and costs.
*   **Stock Control**:
    *   **Recipe Linking**: Map menu items to ingredients for auto-deduction (BOM).
    *   **Costing**: Track unit cost and total cost of incoming inventory.
    *   **Alerts**: Low-stock notifications based on minimum threshold.
    *   **Adjustments**: Manual stock corrections for wastage or breakage (requires Audit Log).

## 4. Workforce Management

**Mapped Entities**: `Employee`, `User`, `Role`, `Shift`, `Attendance`, `Branch`

*   **Employee Profiles**:
    *   Manage personal details (`full_name`, `phone`, `position`) in `EmployeeEntity`.
    *   Link employees to specific `BranchEntity`.
*   **Access Control (RBAC)**:
    *   **Users**: Secure login credentials linked to employees (`UserEntity`).
    *   **Roles**: Define permissions (Admin, Store Manager, Cashier) via `RoleEntity`.
    *   **Branch Scoping**: Ensure users only access data for their assigned branch.
*   **Time & Attendance**:
    *   **Shifts**: Schedule and track start/end times (`ShiftEntity`).
    *   **Clock-in/out**: Record daily attendance (`AttendanceEntity`) with late minute calculation.
    *   **Status**: Track Present, Late, or Absent statuses.

## 5. Customer Loyalty

**Mapped Entities**: `Customer`

*   **CRM**:
    *   Maintain customer profiles (`name`, `phone`, `email`).
    *   Track key dates (`dob`) for birthday promotions.
    *   Status management (Active/Inactive).
*   **History**:
    *   View purchase history linked to `OrderEntity`.
*   **Loyalty Program**:
    *   **Points Accumulation**: Automated earning of points based on order total ($1 = 1 Point).
    *   **Membership Tiers**: Dynamic leveling (Bronze, Silver, Gold) based on total points.
    *   **Redemption**: Use points to apply discounts directly at the POS terminal.

## 6. Reporting & Analytics

**Data Sources**: All Tables

*   **Sales**: Revenue by period, branch, category, and best-selling items.
*   **Financial**: Payment method breakdown, tax collected, and discounts given.
*   **Labor**: Staff punctuality, hours worked, and sales performance per employee.
*   **Inventory**: Stock valuation, waste reports, and supplier purchase history.

## 7. Expense Management

**Mapped Entities**: `Expense`, `Branch`, `User`

*   **Operation Costs**:
    *   Logging of daily expenses (Utilities, Salary, Marketing, etc.).
    *   Categorization for financial analysis.
*   **Profitability Tracking**:
    *   Gross and Net profit calculations in the admin dashboard.