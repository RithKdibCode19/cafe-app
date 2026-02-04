# Frontend Setup Guide

## Prerequisites
* **Node.js**: Version 20 or 22 (LTS)
* **Package Manager**: npm (comes with Node)

## Installation
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```

## Development
To start the hot-reloading development server:
```bash
npm run dev
```
The app will be available at `http://localhost:3000`.

### Backend Proxy
In development mode, requests to `/api/*` are automatically proxied to `http://localhost:8081` (the Spring Boot backend). This is configured in `nuxt.config.ts` under `nitro.devProxy`.

## Production Build
To build the application for production:
```bash
npm run build
```
The output will be in the `.output` directory. You can run it with:
```bash
node .output/server/index.mjs
```

## Linting & Formatting
(Recommended) Use ESLint and Prettier. Run:
```bash
npm run lint
```
