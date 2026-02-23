# Prompt: Generate a Full SDLC Document for "Cofeoshop" — Cafe POS System

> **Instructions for Kimi AI**: You are an expert software engineering technical writer. Generate a **complete Software Development Life Cycle (SDLC) document** for the project described below. The document must be **at least 60 pages long** when rendered as a PDF (A4, 12pt font, 1.5 line spacing). Write in **formal academic English**. Use proper headings, numbered sections, tables, diagrams (described in text or Mermaid syntax), and references. This is for a **Year 3 Semester 2 university assignment**.

---

## PROJECT INFORMATION

- **Project Name**: Cofeoshop — Cafe Point of Sale System
- **Project Type**: Full-Stack Web Application + Mobile App
- **Team Size**: Solo developer (student project)
- **Duration**: ~3 months (December 2025 – February 2026)
- **University**: Year 3, Semester 2 assignment

---

## TECHNOLOGY STACK

### Backend

- **Framework**: Spring Boot 3.4.2
- **Language**: Java 21
- **Build Tool**: Maven 3.9+
- **Database**: PostgreSQL 16
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + JWT (JSON Web Token)
- **Key Libraries**: Lombok 1.18.36, MapStruct 1.6.0, Cloudinary (image uploads), Swagger/OpenAPI 3
- **Architecture Pattern**: Layered (Controller → Service → Repository → Database)

### Frontend (Web)

- **Framework**: Nuxt 4 (Vue 3.5+ with Composition API)
- **Language**: TypeScript
- **Styling**: TailwindCSS
- **State Management**: Nuxt `useState` composables
- **i18n**: @nuxtjs/i18n (English + Khmer)

### Mobile

- **Framework**: Nuxt-based PWA / Mobile Web App

### DevOps / Deployment

- **Containerization**: Docker & Docker Compose
- **Reverse Proxy**: Nginx
- **SSL**: Cloudflare
- **VPS**: 2GB RAM Linux server
- **CI/CD**: Manual deployment via SSH + Docker Compose

---

## SYSTEM MODULES & FEATURES

Please describe ALL of the following modules in detail throughout the document:

### 1. Authentication & Authorization

- JWT-based stateless authentication
- Role-based access control (SuperAdmin, Admin, Manager, Cashier, Staff)
- Permission system with `PermissionEntity`, `RoleEntity`, `RolePermissionEntity`
- Branch-level data isolation using AOP (`@IsolateByBranch` annotation + `BranchSecurityAspect`)
- Customer authentication (separate JWT for mobile customers with `CUSTOMER:` prefix)

### 2. Point of Sale (POS) Module

- Real-time menu display with category hierarchy (root → subcategories)
- Menu item cards with images, prices, variants (S/M/L sizes)
- Add-ons system (extra toppings, etc.)
- Shopping cart with quantity management
- Hold/Resume order functionality
- Customer lookup and association
- Loyalty points redemption at checkout
- Order type support: Dine-in, Takeaway, Delivery
- Manual discount and tax calculation (10% tax)
- Order notes and special instructions
- Shift management (clock in/out, cash drawer)
- Cash drawer actions (open, close, count)

### 3. Order Management

- Order lifecycle: PENDING → PAID → PREPARING → READY → COMPLETED
- Void and Refund with PIN verification and reason tracking
- Order number generation (unique `orderNo`)
- Order search by number, date range, status, branch, cashier
- Paginated order listing with filters
- POS order adjustments (discounts, modifications with approval)

### 4. Menu Management (Admin)

- CRUD for Categories (hierarchical with parent/child)
- CRUD for Menu Items with image upload (Cloudinary)
- Variant management (sizes with different prices)
- Add-on management
- Recipe management (linking ingredients to menu items with quantities)
- Multi-language support: `name` (English) + `nameKh` (Khmer) for categories and menu items

### 5. Inventory & Stock Management

- Ingredient management with units and stock levels
- Branch-specific stock tracking (`BranchStockEntity`)
- Stock-in operations with supplier association
- Stock adjustments (increase/decrease with reasons and approval)
- Stock transfer between branches
- Low stock alerts and indicators on POS
- Inventory audit reports

### 6. Customer & Loyalty System

- Customer registration and profile management
- Loyalty points system (earn on purchases, redeem for discounts)
- Membership levels: Bronze, Silver, Gold, Platinum
- Customer order history
- Points earning rate and redemption rate (configurable via system settings)

### 7. Staff & HR Management

- Employee CRUD (personal info, position, salary, branch assignment)
- Attendance tracking (clock-in/clock-out)
- Mobile check-in via QR code + geolocation verification
- Shift management with cash drawer reconciliation
- Payroll module
- Staff performance reports

### 8. Financial Management

- Expense tracking by category and branch
- Payment processing (Cash, Card, KHQR/Bakong)
- KHQR payment integration with Bakong API
- Cash drawer management
- Revenue and expense reports

### 9. Reporting & Analytics

- Dashboard with KPI cards (total revenue, orders, average order value)
- Sales reports with date range filtering
- Branch performance comparison
- Staff productivity reports
- Inventory audit reports
- Export to Excel functionality

### 10. Kitchen Display System (KDS)

- Real-time order queue for kitchen staff
- Order status updates (Preparing → Ready)
- Separate layout optimized for kitchen monitors

### 11. QR Code Ordering (Public Menu)

- Branch-specific QR code generation
- Public menu page (no authentication required)
- Customer can browse menu, add to cart, and place orders
- Table number association for dine-in
- Guest ordering (no login required, just name + phone)

### 12. Multi-Branch Support

- Multiple branch management
- Branch-specific data isolation (orders, stock, employees)
- Cross-branch stock transfer
- Branch performance comparison in reports

### 13. System Settings

- Configurable system parameters (tax rate, loyalty rates, etc.)
- Stored in `SystemSettingEntity` (key-value pairs)

### 14. Import/Export

- Data import/export functionality
- Excel report generation and download

### 15. Internationalization (i18n)

- English and Khmer language support
- Static UI translation via locale JSON files
- Dynamic data translation (menu items, categories with `nameKh` fields)
- Language switcher component with flag icons

---

## DATABASE ENTITIES (29 Tables)

The system has the following 29 database entities. All extend `BaseEntity` which provides `createdAt`, `updatedAt`, `deletedAt`, `deletedBy` (soft delete support):

| #   | Entity                   | Table Name               | Key Fields                                                                                         |
| --- | ------------------------ | ------------------------ | -------------------------------------------------------------------------------------------------- |
| 1   | UserEntity               | tblusers                 | username, password, role, branchId                                                                 |
| 2   | RoleEntity               | tblroles                 | name, description                                                                                  |
| 3   | PermissionEntity         | tblpermissions           | name, description                                                                                  |
| 4   | RolePermissionEntity     | tblrole_permissions      | roleId, permissionId                                                                               |
| 5   | BranchEntity             | tblbranches              | name, address, latitude, longitude, radius                                                         |
| 6   | EmployeeEntity           | tblemployees             | fullName, phone, position, salary, branchId                                                        |
| 7   | CategoryEntity           | tblcategories            | name, nameKh, description, descriptionKh, parentId                                                 |
| 8   | MenuItemEntity           | tblmenu_items            | name, nameKh, description, descriptionKh, basePrice, imageUrl, categoryId                          |
| 9   | VariantEntity            | tblvariants              | size (S/M/L), price, menuItemId                                                                    |
| 10  | AddOnEntity              | tbladdons                | name, price                                                                                        |
| 11  | IngredientEntity         | tblingredients           | name, unit, costPerUnit                                                                            |
| 12  | RecipeEntity             | tblrecipes               | menuItemId, ingredientId, quantity                                                                 |
| 13  | BranchStockEntity        | tblbranch_stock          | branchId, ingredientId, quantity                                                                   |
| 14  | StockInEntity            | tblstock_in              | ingredientId, quantity, supplierId, branchId                                                       |
| 15  | StockAdjustmentEntity    | tblstock_adjustments     | ingredientId, adjustmentType, quantity, reason, approvedBy                                         |
| 16  | StockTransferEntity      | tblstock_transfers       | ingredientId, fromBranchId, toBranchId, quantity                                                   |
| 17  | SupplierEntity           | tblsuppliers             | name, phone, email, address                                                                        |
| 18  | CustomerEntity           | tblcustomers             | fullName, phone, loyaltyPoints, membershipLevel                                                    |
| 19  | OrderEntity              | tblorders                | orderNo, branchId, cashierUserId, orderType, status, subTotal, taxAmount, totalAmount, orderSource |
| 20  | OrderItemEntity          | tblorder_items           | orderId, menuItemId, variantId, quantity, unitPrice                                                |
| 21  | PaymentEntity            | tblpayments              | orderId, method, amount, transactionRef                                                            |
| 22  | PosOrderAdjustmentEntity | tblpos_order_adjustments | orderId, adjustmentType, amount, reason, approvedById                                              |
| 23  | ExpenseEntity            | tblexpenses              | category, amount, description, branchId                                                            |
| 24  | ShiftEntity              | tblshifts                | userId, branchId, startTime, endTime, openingCash, closingCash                                     |
| 25  | DrawerActionEntity       | tbldrawer_actions        | shiftId, actionType, amount, reason                                                                |
| 26  | AttendanceEntity         | tblattendance            | employeeId, clockIn, clockOut, status                                                              |
| 27  | AuditLogEntity           | tblaudit_logs            | action, entityType, entityId, userId                                                               |
| 28  | SystemSettingEntity      | tblsystem_settings       | key, value, description                                                                            |
| 29  | BaseEntity               | (MappedSuperclass)       | createdAt, updatedAt, deletedAt, deletedBy                                                         |

---

## API CONTROLLERS (35+ endpoints)

The backend exposes RESTful APIs via the following controllers:

| Controller                     | Base Path                 | Purpose                               |
| ------------------------------ | ------------------------- | ------------------------------------- |
| AuthController                 | /api/auth                 | Login, registration                   |
| UserController                 | /api/users                | User CRUD                             |
| RoleController                 | /api/roles                | Role management                       |
| PermissionController           | /api/permissions          | Permission management                 |
| BranchController               | /api/branches             | Branch CRUD                           |
| CategoryController             | /api/categories           | Category CRUD                         |
| MenuItemController             | /api/menu-items           | Menu item CRUD                        |
| VariantController              | /api/variants             | Size variants CRUD                    |
| AddOnController                | /api/addons               | Add-ons CRUD                          |
| IngredientController           | /api/ingredients          | Ingredient CRUD                       |
| RecipeController               | /api/recipes              | Recipe management                     |
| BranchStockController          | /api/branch-stock         | Branch stock levels                   |
| StockInController              | /api/stock-in             | Stock receiving                       |
| StockAdjustmentController      | /api/stock-adjustments    | Stock adjustments                     |
| SupplierController             | /api/suppliers            | Supplier CRUD                         |
| CustomerController             | /api/customers            | Customer management + loyalty         |
| OrderController                | /api/orders               | Order CRUD + status changes           |
| PaymentController              | /api/payments             | Payment processing                    |
| BakongPaymentController        | /api/bakong               | KHQR payment integration              |
| ExpenseController              | /api/expenses             | Expense tracking                      |
| ShiftController                | /api/shifts               | Shift management                      |
| DrawerActionController         | /api/drawer-actions       | Cash drawer operations                |
| AttendanceController           | /api/attendance           | Clock in/out                          |
| EmployeeController             | /api/employees            | Employee management                   |
| ReportController               | /api/reports              | All reports (sales, inventory, staff) |
| ImportExportController         | /api/import-export        | Data import/export                    |
| FileUploadController           | /api/upload               | Image upload (Cloudinary)             |
| SystemSettingController        | /api/settings             | System configuration                  |
| NotificationController         | /api/notifications        | System notifications                  |
| RecommendationController       | /api/recommendations      | Smart suggestions                     |
| ReceiptController              | /api/receipts             | Receipt generation                    |
| PosOrderAdjustmentController   | /api/pos-adjustments      | Order adjustments                     |
| PayrollController              | /api/payroll              | Payroll calculations                  |
| EmployeePerformanceController  | /api/employee-performance | Staff metrics                         |
| BranchInsightController        | /api/branch-insights      | Branch analytics                      |
| (Public) PublicMenuController  | /api/public/menu          | QR menu (no auth)                     |
| (Mobile) MobileAuthController  | /api/mobile/auth          | Customer login (OTP)                  |
| (Mobile) MobileOrderController | /api/mobile/orders        | Mobile ordering                       |

---

## FRONTEND PAGES & LAYOUTS

### Layouts

| Layout  | File                  | Purpose                                                             |
| ------- | --------------------- | ------------------------------------------------------------------- |
| POS     | pos.vue (2351 lines)  | Full POS interface with cart sidebar, shift management, held orders |
| Admin   | admin.vue (736 lines) | Dashboard with sidebar navigation, dark mode toggle                 |
| Kitchen | kitchen.vue           | Kitchen display layout                                              |
| Menu    | menu.vue              | Public QR menu layout                                               |
| Auth    | auth.vue              | Login/registration layout                                           |
| Default | default.vue           | Landing page layout                                                 |

### Key Pages

| Page                | Path                    | Purpose                                 |
| ------------------- | ----------------------- | --------------------------------------- |
| Landing             | /                       | Premium landing page                    |
| Login               | /login                  | Staff authentication                    |
| POS                 | /pos                    | Main point of sale                      |
| Admin Dashboard     | /admin                  | KPI dashboard with charts               |
| Menu Management     | /admin/menu             | CRUD for menu items                     |
| Category Management | /admin/categories       | Category hierarchy                      |
| Order Management    | /admin/orders           | Order listing + status                  |
| Inventory           | /admin/inventory        | Stock levels, adjustments               |
| Staff Management    | /admin/staff            | Employees, attendance                   |
| Customer Management | /admin/customers        | Customer + loyalty                      |
| Reports             | /admin/reports/\*       | Sales, inventory, staff, branch reports |
| Settings            | /admin/settings         | System configuration                    |
| QR Codes            | /admin/qr-codes         | QR code generation                      |
| Kitchen Display     | /kitchen                | Live order queue                        |
| Public Menu         | /menu/[branchCode]      | QR-accessible menu                      |
| Cart                | /menu/[branchCode]/cart | QR order cart                           |
| Staff Terminal      | /staff                  | Employee clock-in page                  |

### Key Composables

| Composable     | Purpose                                                               |
| -------------- | --------------------------------------------------------------------- |
| useApi         | Centralized API client with auth headers, error handling, SSR support |
| useAuth        | JWT token + user state management via cookies                         |
| useCart        | Shopping cart state, add/remove/hold/resume orders, totals            |
| useBranch      | Branch selection state                                                |
| usePermissions | Permission checking                                                   |
| useToast       | Toast notification system                                             |
| useTrans       | Dynamic data translation (nameKh fallback)                            |
| useMenuCart    | Public menu cart (QR ordering)                                        |
| usePublicApi   | API client for public (unauthenticated) endpoints                     |

---

## DOCUMENT STRUCTURE (Required Sections)

Generate the document with the following chapters. **Each chapter should be detailed and thorough to reach the 60-page target.**

### Chapter 1: Introduction (4-5 pages)

- 1.1 Project Background & Motivation
- 1.2 Problem Statement (challenges of manual cafe management in Cambodia)
- 1.3 Objectives (General + Specific)
- 1.4 Scope & Limitations
- 1.5 Significance of the Project
- 1.6 Document Overview

### Chapter 2: Literature Review (5-6 pages)

- 2.1 Point of Sale Systems Overview
- 2.2 Web-based POS vs Traditional POS
- 2.3 Related Technologies (Spring Boot, Nuxt.js, PostgreSQL, Docker)
- 2.4 Related Works / Existing Systems Comparison
- 2.5 SDLC Methodologies (Agile/Iterative approach used)

### Chapter 3: Requirement Analysis (8-10 pages)

- 3.1 Functional Requirements (for EACH module listed above — POS, Orders, Menu, Inventory, etc.)
- 3.2 Non-Functional Requirements (performance, security, scalability, usability, i18n)
- 3.3 Use Case Diagrams (describe at least 8-10 use cases with actors: SuperAdmin, Admin, Cashier, Customer, Kitchen Staff)
- 3.4 Use Case Descriptions (detailed table format for each use case)
- 3.5 User Stories

### Chapter 4: System Design (10-12 pages)

- 4.1 System Architecture Diagram (3-tier: Browser → Nuxt SSR → Spring Boot → PostgreSQL)
- 4.2 Component Diagram
- 4.3 Entity-Relationship Diagram (describe all 29 entities and their relationships)
- 4.4 Database Schema Design (table with all columns, types, constraints — use the entity list above)
- 4.5 Class Diagram (Backend: Controller, Service, Repository, Entity, DTO, Mapper layers)
- 4.6 Sequence Diagrams (at least 5: Login, Create Order, Process Payment, Stock Adjustment, QR Order)
- 4.7 API Design (RESTful endpoints table with methods, paths, request/response)
- 4.8 Security Design (JWT flow, branch isolation, role-based access)
- 4.9 UI/UX Design (describe the premium dark theme POS, glassmorphism admin dashboard, responsive design)

### Chapter 5: Implementation (10-12 pages)

- 5.1 Development Environment Setup
- 5.2 Backend Implementation
  - 5.2.1 Project Structure (layered architecture)
  - 5.2.2 Entity & DTO Design with MapStruct
  - 5.2.3 Security Implementation (JWT, Branch Isolation AOP)
  - 5.2.4 Business Logic (Order processing, Stock management, Loyalty system)
  - 5.2.5 Payment Integration (KHQR/Bakong)
  - 5.2.6 Reporting Engine
- 5.3 Frontend Implementation
  - 5.3.1 Nuxt 4 Project Structure
  - 5.3.2 Composables Architecture (useApi, useAuth, useCart, etc.)
  - 5.3.3 POS Interface (dark theme, category navigation, cart management)
  - 5.3.4 Admin Dashboard (sidebar navigation, data tables, charts)
  - 5.3.5 Kitchen Display System
  - 5.3.6 QR Code Ordering System
  - 5.3.7 Internationalization (i18n with dynamic data translation)
- 5.4 Mobile App Implementation
- 5.5 Key Code Snippets (show important patterns with explanations)

### Chapter 6: Deployment (4-5 pages)

- 6.1 Docker Containerization (multi-service Docker Compose)
- 6.2 Production Docker Compose Configuration
- 6.3 Nginx Reverse Proxy Setup
- 6.4 SSL/TLS with Cloudflare
- 6.5 VPS Deployment (2GB RAM optimization)
- 6.6 Deployment Architecture Diagram

### Chapter 7: Testing & Quality Assurance (5-6 pages)

- 7.1 Testing Strategy
- 7.2 Unit Testing
- 7.3 Integration Testing
- 7.4 Manual Testing (test cases table with steps, expected results)
- 7.5 User Acceptance Testing
- 7.6 Performance Considerations
- 7.7 Security Testing
- 7.8 Bug Tracking & Resolution

### Chapter 8: Results & Discussion (4-5 pages)

- 8.1 System Screenshots (describe key screens: Landing, Login, POS, Admin Dashboard, Kitchen, QR Menu)
- 8.2 Feature Completion Summary (table: planned vs implemented)
- 8.3 Performance Results
- 8.4 User Feedback
- 8.5 Challenges Faced & Solutions
- 8.6 Comparison with Existing Solutions

### Chapter 9: Conclusion & Future Work (3-4 pages)

- 9.1 Summary of Achievements
- 9.2 Lessons Learned
- 9.3 Future Enhancements (real-time WebSocket, advanced analytics, mobile native app, multi-currency)
- 9.4 Final Remarks

### References (2-3 pages)

- Include at least 15-20 references (academic papers, official documentation, books)
- Spring Boot docs, Vue.js docs, PostgreSQL docs, Docker docs, OWASP, etc.

### Appendices (3-5 pages)

- A. Full Database Schema
- B. API Endpoint Reference
- C. Sample Screenshots
- D. Deployment Configuration Files

---

## FORMATTING REQUIREMENTS

1. **Length**: Minimum 60 pages (aim for 65-70 to be safe)
2. **Font**: Times New Roman or similar, 12pt
3. **Spacing**: 1.5 line spacing
4. **Margins**: 1 inch on all sides
5. **Headings**: Numbered (1, 1.1, 1.1.1)
6. **Tables**: Use tables extensively for requirements, test cases, API endpoints, database schema
7. **Diagrams**: Describe diagrams in detail or provide Mermaid syntax. Include:
   - System Architecture Diagram
   - ER Diagram
   - Use Case Diagrams
   - Sequence Diagrams (at least 5)
   - Deployment Diagram
   - Class Diagram
8. **Academic Tone**: Formal, third-person, professional language
9. **Page Numbers**: Include
10. **Table of Contents**: Include at the beginning
11. **List of Figures**: Include
12. **List of Tables**: Include

---

## IMPORTANT CONTEXT

- This is a **real, working system** — not a theoretical project. All features described above are implemented and functional.
- The system is deployed on a production VPS at a real domain.
- The POS interface has a **premium dark theme** with smooth animations, glassmorphism effects, and responsive design.
- The admin dashboard has a **macOS-inspired design** with sidebar navigation, dark mode support, and interactive charts.
- The system supports the **Cambodian market** with KHQR payment integration (Bakong) and Khmer language support.
- Branch isolation ensures that each branch can only see its own data (orders, stock, employees) unless the user is a SuperAdmin.

---

## FINAL INSTRUCTION

Generate the **complete document** with all sections filled with detailed, substantive content. Do not use placeholder text like "Lorem ipsum" or "[Insert here]". Every section should contain real, meaningful content based on the project description above. Use tables, bullet points, and structured formatting to maximize readability and page count. Include Mermaid diagram code blocks where applicable.
