# Production Deployment Guide

## 1. Domain Configuration

### DNS Setup
1. Log in to your domain registrar (Namecheap, GoDaddy, Cloudflare, etc.)
2. Add an **A Record**:
   - **Host/Name**: `@` (or `www`)
   - **Value/Content**: Your VPS IP Address (e.g., `123.45.67.89`)
   - **TTL**: Auto or 3600

### Nginx Configuration
1. Open `deployment/nginx/nginx.conf`
2. Update `server_name` in the SSL block:
   ```nginx
   server {
       listen 443 ssl;
       server_name your-cafe-app.com;  # <--- UPDATE THIS
       ...
   }
   ```

---

## 2. SSL Certificates (HTTPS)

This setup expects you to provide your own SSL certificates. The easiest way is using **Cloudflare** (Free).

### Option A: Cloudflare (Recommended)
1. Add your domain to Cloudflare.
2. Go to **SSL/TLS** > **Origin Server**.
3. Click "Create Certificate".
4. Copy the **Origin Certificate** content to a file named `cert.pem`.
5. Copy the **Private Key** content to a file named `key.pem`.
6. Upload them to the VPS:
   ```bash
   # On your local machine:
   scp cert.pem key.pem user@your-vps-ip:~/cafe-app/deployment/certs/
   ```

### Option B: Self-Signed (Testing Only)
If you don't have a domain yet, generate a self-signed cert:
```bash
cd deployment/certs
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes
```

---

## 3. Deploy

1. SSH into your VPS.
2. Navigate to deployment folder:
   ```bash
   cd ~/cafe-app/deployment
   ```
3. Create `.env` from example:
   ```bash
   cp .env.prod.example .env
   nano .env  # Update database passwords etc.
   ```
4. Start services:
   ```bash
   docker-compose -f docker-compose.prod.yml up -d --build
   ```
