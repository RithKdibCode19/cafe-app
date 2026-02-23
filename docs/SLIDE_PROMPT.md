# Prompt: Generate Professional Presentation Slides for "Cofeoshop" Cafe POS System

> **Instructions**: You are a professional presentation designer. Generate a complete slide deck outline for a university project defense presentation. Each slide should have a **title**, **3-5 bullet points** (concise, no full sentences), and a **speaker note** suggestion. The presentation should be **20-25 slides**, professional, modern, and visually clean. Use this content to create slides in PowerPoint, Google Slides, or Gamma.app.

---

## SLIDE 1: Title Slide

- **Title**: Cofeoshop — Cafe Point of Sale System
- **Subtitle**: Year 3, Semester 2 — Software Development Project
- **Student Name**: [Your Name]
- **Date**: February 2026
- **Visual**: Logo / Coffee icon

---

## SLIDE 2: Agenda

- Introduction & Problem Statement
- Objectives
- Technology Stack
- System Architecture
- Key Features Demo
- Database Design
- Security & Authentication
- Deployment
- Challenges & Lessons Learned
- Conclusion & Future Work

---

## SLIDE 3: Problem Statement

- Manual cafe operations → slow, error-prone
- No real-time inventory tracking across branches
- Cash reconciliation done on paper
- No customer loyalty tracking
- Language barrier (English only, no Khmer support)

---

## SLIDE 4: Project Objectives

- Build a full-stack POS system for multi-branch cafes
- Real-time inventory & stock management
- Integrated payment processing (Cash, Card, KHQR)
- Customer loyalty & membership system
- Bilingual support (English + Khmer)
- Cloud-deployed, mobile-accessible

---

## SLIDE 5: Technology Stack

| Layer        | Technology                              |
| ------------ | --------------------------------------- |
| Backend      | Spring Boot 3.4, Java 21, PostgreSQL 16 |
| Frontend     | Nuxt 4 (Vue 3), TypeScript, TailwindCSS |
| Security     | Spring Security + JWT                   |
| Deployment   | Docker, Nginx, Cloudflare SSL           |
| Integrations | Bakong KHQR, Cloudinary                 |

---

## SLIDE 6: System Architecture

- **3-Tier Architecture**: Browser → Nuxt SSR → Spring Boot API → PostgreSQL
- Docker containers for all services
- Nginx reverse proxy with SSL
- REST API communication (JSON)
- **Visual**: Architecture diagram (Browser → Frontend → Backend → DB)

---

## SLIDE 7: Database Design (ER Overview)

- **29 tables** covering all business domains
- Soft delete support (deleted_at, deleted_by)
- Key relationships:
  - Users → Roles → Permissions (RBAC)
  - Orders → OrderItems → MenuItems → Variants
  - Ingredients → Recipes → MenuItems
  - BranchStock per branch per ingredient
- **Visual**: ER diagram screenshot from dbdiagram.io

---

## SLIDE 8: Core Module — POS Interface

- Premium dark-theme UI optimized for speed
- Category hierarchy navigation (root → sub)
- Menu items with images, variants (S/M/L), add-ons
- Quick search with real-time filtering
- Hold/Resume multiple orders
- **Visual**: POS screenshot

---

## SLIDE 9: Core Module — Order Management

- Order lifecycle: PENDING → PAID → PREPARING → READY → COMPLETED
- Void & Refund with PIN verification
- Order types: Dine-in, Takeaway, Delivery
- Source tracking: POS, Mobile, QR Web
- Paginated order history with filters

---

## SLIDE 10: Core Module — Inventory & Stock

- Ingredient tracking with SKU codes
- Branch-level stock isolation
- Stock-in from suppliers with cost tracking
- Stock adjustments (Wastage, Damage, Expired) with approval workflow
- Inter-branch stock transfer
- Low stock alerts on POS

---

## SLIDE 11: Core Module — Admin Dashboard

- macOS-inspired design with collapsible sidebar
- KPI cards: Revenue, Orders, Avg. Order Value
- Interactive charts and reports
- Dark mode toggle
- Role-based navigation (SuperAdmin sees everything)
- **Visual**: Dashboard screenshot

---

## SLIDE 12: Payment Integration — KHQR

- Bakong API integration for QR payments
- Generate dynamic QR codes per transaction
- Payment status: PENDING → PAID → FAILED → REFUNDED
- Supports: Cash, Card, QR
- Transaction ID tracking for reconciliation

---

## SLIDE 13: Customer & Loyalty System

- Customer registration with phone/email
- Loyalty points earned on every purchase
- Membership tiers: Bronze → Silver → Gold → Platinum
- Points redemption at checkout (configurable rate)
- Customer order history

---

## SLIDE 14: QR Code Ordering

- Branch-specific QR codes generated in admin
- Customers scan → browse menu → order (no app needed)
- Guest ordering: just name + phone
- Table number association for dine-in
- Orders appear on Kitchen Display & POS
- **Visual**: QR menu mobile screenshot

---

## SLIDE 15: Kitchen Display System (KDS)

- Real-time order queue for kitchen staff
- Status updates: Preparing → Ready
- Optimized layout for kitchen monitors
- Auto-refresh for new orders
- Color-coded priority indicators

---

## SLIDE 16: Staff & HR Management

- Employee profiles with branch assignment
- Attendance tracking (clock-in/clock-out)
- Mobile QR check-in with geolocation verification
- Shift management with cash drawer reconciliation
- Staff performance reports

---

## SLIDE 17: Security Architecture

- **JWT** stateless authentication (24h expiry)
- **Role-Based Access Control** (5 roles, fine-grained permissions)
- **Branch Data Isolation** via AOP (`@IsolateByBranch`)
  - Non-SuperAdmin users can only access their branch's data
- BCrypt password hashing
- Separate customer JWT tokens (prefix: `CUSTOMER:`)

---

## SLIDE 18: Internationalization (i18n)

- Bilingual: English + Khmer
- Static UI translations via JSON locale files
- **Dynamic data translation**: `nameKh`, `descriptionKh` fields in DB
- `useTrans()` composable: auto-switches based on locale
- Language switcher with flag icons in header
- **Visual**: Side-by-side EN vs KH screenshot

---

## SLIDE 19: Deployment Architecture

- **Docker Compose** multi-service setup
- Services: PostgreSQL, Spring Boot, Nuxt, Nginx
- Optimized for 2GB RAM VPS
- Cloudflare DNS + SSL/TLS
- Health checks on all containers
- **Visual**: Deployment diagram

---

## SLIDE 20: Reports & Analytics

- Sales reports (daily, weekly, monthly)
- Branch performance comparison
- Staff productivity metrics
- Inventory audit reports
- **Export to Excel** for all reports
- Date range filtering

---

## SLIDE 21: Technical Highlights

- **MapStruct** for zero-boilerplate DTO mapping
- **AOP** for cross-cutting concerns (branch isolation)
- **Nuxt composables** for reusable logic (useApi, useAuth, useCart)
- SSR-aware API calls (server vs client base URL)
- Cloudinary for image uploads
- Swagger/OpenAPI documentation

---

## SLIDE 22: Challenges & Solutions

| Challenge                  | Solution                                       |
| -------------------------- | ---------------------------------------------- |
| Lombok + Java 21 conflicts | Pinned to Java 21, manual constructors         |
| SSR API routing in Docker  | Dual base URL config (internal vs public)      |
| Branch data leakage        | AOP-based `@IsolateByBranch`                   |
| 2GB RAM deployment         | Optimized JVM flags, multi-stage Docker builds |
| Real-time kitchen updates  | Polling-based refresh (future: WebSocket)      |

---

## SLIDE 23: Demo

- Live demo or video walkthrough
- Show: Login → POS → Place Order → Kitchen → Admin Dashboard → Reports
- **Visual**: Screenshots or live screen share

---

## SLIDE 24: Future Enhancements

- WebSocket real-time updates (Kitchen, POS)
- Native mobile app (React Native / Flutter)
- Advanced analytics with AI recommendations
- Multi-currency support (USD + KHR)
- Email/SMS notifications
- Offline mode for POS

---

## SLIDE 25: Conclusion

- Successfully built a **production-ready** multi-branch POS
- Covers full business workflow: Menu → Order → Payment → Inventory → Reports
- Deployed on real VPS with SSL
- Supports Cambodian market (KHQR + Khmer language)
- **Thank you! Questions?**

---

## DESIGN TIPS FOR THE SLIDES

1. **Color Palette**: Use dark navy (#0D0D0D) + Primary Orange/Amber (#F97316) — matches the POS theme
2. **Font**: Use "Plus Jakarta Sans" or "Inter" for headings, clean sans-serif for body
3. **Layout**: One key idea per slide, max 5 bullet points
4. **Icons**: Use Lucide or Heroicons style for consistency
5. **Screenshots**: Add actual app screenshots where indicated
6. **Diagrams**: Use the ER diagram from dbdiagram.io and architecture diagrams

## RECOMMENDED TOOLS

- **Gamma.app** — Paste this prompt directly and it will auto-generate beautiful slides
- **Google Slides** — Use a dark theme template
- **Canva** — "Pitch Deck" templates work well
- **Beautiful.ai** — Auto-layouts for professional look
