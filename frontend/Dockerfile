# Multi-stage build for Nuxt application

# Stage 1: Build stage
FROM node:22-alpine AS build
WORKDIR /app

# Copy package files for better caching
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build the Nuxt application
RUN npm run build

# Stage 2: Runtime stage
FROM node:22-alpine
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S nuxt && adduser -S nuxt -G nuxt

# Copy built application from build stage
COPY --from=build /app/.output ./.output
COPY --from=build /app/package*.json ./

# Change ownership to the nuxt user
RUN chown -R nuxt:nuxt /app

USER nuxt

# Expose the application port
EXPOSE 3000

# Set environment variables
ENV HOST=0.0.0.0
ENV PORT=3000
ENV NODE_ENV=production

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:3000 || exit 1

# Run the application
CMD ["node", ".output/server/index.mjs"]
