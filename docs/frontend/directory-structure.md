# Frontend Directory Structure (Nuxt 4)

We follow the **Nuxt 4** convention using the `app/` directory to keep the project root clean.

```
frontend/
├── app/                  # Main Source Code
│   ├── assets/           # CSS, Fonts, Images
│   │   └── css/          # Tailwind & Global Styles
│   ├── components/       # Vue Components
│   │   └── ui/           # Reusable UI Elements (Buttons, Inputs)
│   ├── composables/      # Shared Logic (Hooks)
│   │   └── useApi.ts     # API Wrapper
│   ├── layouts/          # Page Layouts
│   │   ├── default.vue   # Standard layout
│   │   ├── admin.vue     # Dashboard layout
│   │   ├── pos.vue       # POS Terminal layout
│   │   └── auth.vue      # Login/Register layout
│   ├── pages/            # Application Routes
│   │   ├── index.vue     # Landing Page
│   │   ├── admin/        # Admin Routes
│   │   ├── pos/          # POS Routes
│   │   └── auth/         # Authentication Routes
│   └── app.vue           # Root Component
├── public/               # Static Files (favicon, etc.)
├── nuxt.config.ts        # Nuxt Configuration
├── tailwind.config.ts    # Tailwind Configuration
└── package.json          # Dependencies
```

## Key Files

### `composables/useApi.ts`
A wrapper around Nuxt's `$fetch` to standardize API calls to the Spring Boot backend. It automatically prepends the `API_BASE` URL.

### `layouts/`
* **admin.vue**: Contains the sidebar navigation and top bar for the management dashboard.
* **pos.vue**: A specialized layout for the Point of Sale interface, maximizing screen real estate for the grid and cart.

### `app/app.vue`
The entry point. It simply renders `<NuxtPage />` (and `<NuxtRouteAnnouncer />` for accessibility).
