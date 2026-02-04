# Database Schema

## ER Diagram (Conceptual)

```mermaid
erDiagram
    Branch ||--o{ Employee : "employs"
    Branch ||--o{ Order : "manages"
    Branch ||--o{ Shift : "hosts"

    Employee ||--o| User : "has login"
    Employee ||--o{ Attendance : "records"
    Employee ||--o{ Shift : "works"
    Branch ||--o{ Expense : "records"
    User ||--o{ Expense : "manages"

    User ||--o{ Order : "processes (cashier)"
    Role ||--o{ User : "assigned to"

    Customer ||--o{ Order : "places"

    Order ||--|{ OrderItem : "contains"
    Order ||--o{ Payment : "is paid by"

    Category ||--o{ MenuItem : "classifies"
    MenuItem ||--o{ Variant : "has variants"
    MenuItem ||--o{ OrderItem : "is part of"
    Variant ||--o{ OrderItem : "specified in"

    Supplier ||--o{ StockIn : "supplies"
    
    %% Entity Definitions

    Branch {
        bigint branch_id PK
        string code
        string name
        string phone
        string location
    }

    Employee {
        bigint employee_id PK
        string full_name
        string phone
        string position
        string status
        bigint branch_id FK
    }

    User {
        bigint user_id PK
        string user_name
        string password
        bigint employee_id FK
        bigint role_id FK
    }

    Role {
        bigint role_id PK
        string role_name
        string description
    }

    Customer {
        bigint customer_id PK
        string name
        string phone
        string email
        string gender
        date dob
        string notes
        string status
        int loyalty_points
        string membership_level
    }

    Category {
        bigint category_id PK
        string name
        string description
    }

    MenuItem {
        bigint menu_item_id PK
        string name
        double base_price
        string image_url
        bigint category_id FK
    }

    Variant {
        bigint variant_id PK
        string size
        double price
        bigint menu_item_id FK
    }

    AddOn {
        bigint addon_id PK
        string name
        double price
    }

    Order {
        bigint order_id PK
        string order_no
        string order_type
        string status
        string note
        bigint branch_id FK
        bigint cashier_user_id FK
        bigint customer_id FK
    }

    OrderItem {
        bigint order_item_id PK
        int qty
        double unit_price
        double discount_amount
        string addons
        bigint order_id FK
        bigint menu_item_id FK
        bigint variant_id FK
    }

    Payment {
        bigint payment_id PK
        string method
        double paid_amount
        double change_amount
        string payment_status
        timestamp paid_at
        bigint order_id FK
    }

    Attendance {
        bigint attendance_id PK
        timestamp check_in
        timestamp check_out
        int late_minute
        string status
        string note
        bigint employee_id FK
    }

    Shift {
        bigint shift_id PK
        timestamp shift_start
        timestamp shift_end
        bigint employee_id FK
        bigint branch_id FK
    }

    Supplier {
        bigint supplier_id PK
        string name
        string phone
        string email
        string gender
        string address
        string notes
        string status
    }

    StockIn {
        bigint stock_id PK
        double qty_in
        double unit_cost
        double total_cost
        string invoice_no
        timestamp received_date
        bigint received_by
        bigint supplier_id FK
        bigint ingredient_id FK
    }

    Expense {
        bigint expense_id PK
        string title
        string description
        double amount
        date date
        string category
        bigint branch_id FK
        bigint recorded_by FK
    }
```

## Table Specifications

### Core Tables
| Table Name | Entity | Description |
|------------|--------|-------------|
| `tblbranches` | `BranchEntity` | Physical cafe locations. |
| `tblemployees` | `EmployeeEntity` | Staff members details. |
| `tblusers` | `UserEntity` | System login credentials linked to employees. |
| `tblroles` | `RoleEntity` | User roles (Admin, Cashier, etc.) for permission control. |
| `tblcustomers` | `CustomerEntity` | Registered loyalty customers. |

### Menu & Inventory
| Table Name | Entity | Description |
|------------|--------|-------------|
| `tblcategories` | `CategoryEntity` | Menu categories (Coffee, Tea, Pastry). |
| `tblmenu_items` | `MenuItemEntity` | Base products. |
| `tblvariants` | `VariantEntity` | Product size variations (S, M, L). |
| `tbladdons` | `AddOnEntity` | Extra items (Sugar, Shot). |
| `tblsuppliers` | `SupplierEntity` | External vendors for ingredients. |
| `tblstock` | `StockInEntity` | Inventory intake logs. |

### Sales & Operations
| Table Name | Entity | Description |
|------------|--------|-------------|
| `tblorders` | `OrderEntity` | Main transaction record. |
| `tbl_order_items` | `OrderItemEntity` | Individual items within an order. |
| `tblpayments` | `PaymentEntity` | Payment transactions and records. |
| `tblshifts` | `ShiftEntity` | Employee work schedules. |
| `tblattendance` | `AttendanceEntity` | Actual clock-in/clock-out records. |
| `tblexpenses` | `ExpenseEntity` | Record of daily store operation costs. |

## Naming Conventions
*   **Tables**: `snake_case`, prefixed with `tbl` (e.g., `tblbranches`, `tblusers`) except join tables (e.g., `tbl_order_items`).
*   **Columns**: `snake_case` matching Java camelCase fields (e.g., `fullName` -> `full_name`).
*   **Primary Keys**: `{entity_name}_id` (e.g., `branch_id`).
*   **Foreign Keys**: `{referenced_entity}_id`.
