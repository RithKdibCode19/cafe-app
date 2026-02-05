# Deployment Guide (2GB RAM VPS)

This guide will help you deploy the Cafe App to a VPS with only 2GB of RAM. Steps include setting up Swap memory (crucial), installing Docker, and deploying with the optimized configuration.

## Prerequisites

- A VPS with Ubuntu/Debian (Recommended).
- Domain registered on Namecheap.
- Cloudflare checking account active.
- SSH access to your VPS.

## Step 1: VPS Preparation (CRITICAL: SWAP Setup)

Since 2GB RAM is tight for Java + Postgres + Node.js, you **MUST** create a Swap file to prevent crashes.

Run these commands on your VPS:

```bash
# Check if swap exists
sudo swapon --show

# If empty, create a 2GB swap file
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# Make it permanent
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# Adjust swappiness (prefer RAM over swap)
sudo sysctl vm.swappiness=10
echo 'vm.swappiness=10' | sudo tee -a /etc/sysctl.conf
```

## Step 2: Install Docker & Compose

```bash
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

# Install Docker
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

## Step 3: Project Setup on VPS

1.  **Clone your repository** or copy the files to the server.

    ```bash
    git clone <your-repo-url> cafe-app
    cd cafe-app
    ```

2.  **Environment Configuration**:
    ```bash
    cd deployment
    cp .env.prod.example .env
    nano .env
    ```
    _Edit `.env` and set strict passwords!_

## Step 4: Cloudflare & SSL (Strict Mode)

We are using **Cloudflare Origin Certificates** for end-to-end encryption.

1.  **Generate Certificate**:
    - Go to Cloudflare Dashboard -> Your Domain -> **SSL/TLS** -> **Origin Server**.
    - Click **Create Certificate**.
    - Keep default settings (RSA, 15 years validity).
    - Click **Create**.

2.  **Save Certificates on VPS**:
    You will see the "Origin Certificate" and "Private Key".

    ```bash
    # On your VPS, inside the deployment folder:
    mkdir -p certs

    # Create the certificate file
    nano certs/cert.pem
    # PASTE the "Origin Certificate" content here. Save & Exit.

    # Create the key file
    nano certs/key.pem
    # PASTE the "Private Key" content here. Save & Exit.
    ```

3.  **Configure Cloudflare SSL Mode**:
    - Go to **SSL/TLS** -> **Overview**.
    - Set encryption mode to **Full (Strict)**.

## Step 5: Launch

```bash
# From the `deployment` directory
docker compose -f docker-compose.prod.yml up -d --build
```

## Step 6: Verify

- Check logs: `docker compose -f docker-compose.prod.yml logs -f`
- Check stats: `docker stats` (Ensure memory usage is within limits)
- Visit your domain `http://your-domain.com`

---

> [!WARNING]
> If `cafe-backend` crashes with "OOM Killed", you may need to increase the Swap size or further reduce JVM Heap in `docker-compose.prod.yml` (`-Xmx512m` -> `-Xmx384m`).
