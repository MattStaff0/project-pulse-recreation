# Azure Deployment Guide — Project Pulse

This document covers the full Azure deployment setup for Project Pulse, including all resource details, configuration changes, and redeployment steps.

---

## Architecture Overview

```
┌─────────────────────────────┐     ┌──────────────────────────────────┐
│  Azure Static Web Apps      │     │  Azure App Service (Linux)       │
│  (Frontend - Vue 3)         │────>│  (Backend - Spring Boot, Java 21)│
│  FREE tier                  │     │  Basic B1 tier (~$13/mo)         │
└─────────────────────────────┘     └──────────────┬───────────────────┘
                                                   │
                                    ┌──────────────▼───────────────────┐
                                    │  Azure SQL Database              │
                                    │  (Free offer tier)               │
                                    │  Serverless, auto-pause          │
                                    └──────────────────────────────────┘
```

**Important:** The production database is Azure SQL (Microsoft SQL Server), NOT MySQL. Local development still uses MySQL via Docker. The Spring Boot app uses profile-based configuration (`dev` for local, `prod` for Azure) to handle this difference.

---

## Azure Resources

All resources are in the **`project-pulse-rg`** resource group.

| Resource | Azure Service | Name | Location | Tier |
|----------|--------------|------|----------|------|
| Resource Group | Resource Group | `project-pulse-rg` | Central US | — |
| Database Server | Azure SQL Server | `project-pulse-sql-tcu2026` | Central US | — |
| Database | Azure SQL Database | `project-pulse` | Central US | Free |
| App Service Plan | App Service Plan | `project-pulse-plan` | Central US | Basic B1 |
| Backend API | App Service | `project-pulse-api-tcu2026` | Central US | Basic B1 |
| Frontend | Static Web App | `project-pulse-frontend` | Central US | Free |

### URLs

| Service | URL |
|---------|-----|
| Backend API | `https://project-pulse-api-tcu2026.azurewebsites.net` |
| Backend Health | `https://project-pulse-api-tcu2026.azurewebsites.net/actuator/health` |
| Frontend | `https://nice-moss-0c4b98110.7.azurestaticapps.net` |

---

## Azure Account Details

- **Subscription**: Azure for Students ($100 credit)
- **Tenant**: Texas Christian University (`6e2d7d24-bbe9-43f7-a31f-fd753eb4d4aa`)
- **Subscription ID**: `23f23b35-c19f-43a2-8cfe-770aae8bb39a`
- **Login**: TCU email (`@tcu.edu`)
- **CLI login requires tenant flag**: `az login --tenant 6e2d7d24-bbe9-43f7-a31f-fd753eb4d4aa`

### TCU Policy Restrictions

The TCU Azure for Students subscription has policy restrictions:
- **MySQL Flexible Server is blocked** — cannot create in any region
- **Region restrictions** — not all Azure regions are allowed; Central US works for App Service and SQL
- Database resources were created via the **Azure Portal UI** because CLI kept hitting policy denials

---

## Database Credentials

| Field | Value |
|-------|-------|
| Server | `project-pulse-sql-tcu2026.database.windows.net` |
| Database | `project-pulse` |
| Admin User | `pulseadmin` |
| Password | `WebTech123` |
| Port | `1433` |
| Connection String | `jdbc:sqlserver://project-pulse-sql-tcu2026.database.windows.net:1433;database=project-pulse;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;` |

---

## Production Login Credentials

On first deploy, a `ProdDataInitializer` seeds the same accounts as the dev environment (only runs if the database is empty):

| Role | Email | Password |
|------|-------|----------|
| Admin | `b.wei@abc.edu` | `123456` |
| Instructor | `s.johnson@abc.edu` | `123456` |
| Student | `j.smith@abc.edu` | `123456` |
| Student | `e.davis@abc.edu` | `123456` |
| Student | `m.brown@abc.edu` | `123456` |
| Student | `s.wilson@abc.edu` | `123456` |
| Student | `d.taylor@abc.edu` | `123456` |
| Student | `l.anderson@abc.edu` | `123456` |

---

## App Service Environment Variables

These are set on the Azure App Service (`project-pulse-api-tcu2026`):

| Variable | Value |
|----------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `AZURE_SQL_USER` | `pulseadmin` |
| `AZURE_SQL_PASSWORD` | `WebTech123` |
| `FRONTEND_URL` | `https://nice-moss-0c4b98110.7.azurestaticapps.net` |

To update these:

```bash
az webapp config appsettings set --resource-group project-pulse-rg --name project-pulse-api-tcu2026 --settings KEY=VALUE
```

---

## Configuration Files Changed for Azure

### 1. `backend/pom.xml`

Added two dependencies for Azure SQL support (alongside existing MySQL deps):

```xml
<!-- SQL Server JDBC driver (for Azure SQL in production) -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Flyway SQL Server support (for production) -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-sqlserver</artifactId>
</dependency>
```

### 2. `backend/src/main/resources/application-prod.yml` (NEW)

Production Spring Boot profile that connects to Azure SQL instead of MySQL:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://project-pulse-sql-tcu2026.database.windows.net:1433;database=project-pulse;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
    username: ${AZURE_SQL_USER:pulseadmin}
    password: ${AZURE_SQL_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: false
  flyway:
    enabled: false
  autoconfigure:
    exclude: org.springframework.boot.actuate.autoconfigure.mail.MailHealthContributorAutoConfiguration
  mail:
    host: ${MAIL_HOST:localhost}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

front-end:
  url: ${FRONTEND_URL:https://project-pulse-frontend.azurestaticapps.net}
```

Key differences from dev:
- Uses SQL Server driver and connection string instead of MySQL
- Flyway disabled (uses `ddl-auto: update` for schema management)
- Mail health check disabled (no mail server configured for prod)
- Database credentials come from environment variables

### 3. `backend/src/main/java/team/projectpulse/system/ProdDataInitializer.java` (NEW)

Minimal data seeder that only creates an admin account if the database is empty. Annotated with `@Profile("prod")`.

### 4. `frontend/.env.production`

```
VITE_SERVER_URL=https://project-pulse-api-tcu2026.azurewebsites.net/api/v1
```

### 5. `frontend/dist/staticwebapp.config.json`

Created in the build output folder for Vue Router SPA support:

```json
{
  "navigationFallback": {
    "rewrite": "/index.html",
    "exclude": ["/assets/*"]
  }
}
```

**Note:** This file lives in `dist/` (build output) and must be recreated after each `npm run build`. To make it permanent, also place it in `frontend/public/staticwebapp.config.json` — Vite copies `public/` contents to `dist/` automatically.

---

## How to Redeploy

### Backend (after code changes)

```bash
# 1. Login to Azure (only needed once per session)
az login --tenant 6e2d7d24-bbe9-43f7-a31f-fd753eb4d4aa

# 2. Build the JAR
cd backend
./mvnw clean package -DskipTests

# 3. Deploy
az webapp deploy --resource-group project-pulse-rg --name project-pulse-api-tcu2026 --src-path target/project-pulse-0.0.1-SNAPSHOT.jar --type jar

# 4. Verify (wait 2-3 min for startup, longer if DB was paused)
# Visit: https://project-pulse-api-tcu2026.azurewebsites.net/actuator/health
```

### Frontend (after code changes)

**Deployment Token:** `8b3598b586bedb76edd7ce21c4b17c3e071a4f034e2d1484593d22e5a20d81cc07-e6dcdc2a-9d94-4614-b654-0ce8160c978801024280c4b98110`

```bash
# 1. Build
cd frontend
npm run build

# 2. Deploy
swa deploy dist --deployment-token 8b3598b586bedb76edd7ce21c4b17c3e071a4f034e2d1484593d22e5a20d81cc07-e6dcdc2a-9d94-4614-b654-0ce8160c978801024280c4b98110 --env production
```

### Full Redeploy (backend + frontend, single command)

```bash
cd /Users/matt_staff/Desktop/Tcu\ computer\ science/Web\ Technologies/project-pulse-recreation/backend && ./mvnw clean package -DskipTests && az webapp deploy --resource-group project-pulse-rg --name project-pulse-api-tcu2026 --src-path target/project-pulse-0.0.1-SNAPSHOT.jar --type jar && cd /Users/matt_staff/Desktop/Tcu\ computer\ science/Web\ Technologies/project-pulse-recreation/frontend && npm run build && swa deploy dist --deployment-token 8b3598b586bedb76edd7ce21c4b17c3e071a4f034e2d1484593d22e5a20d81cc07-e6dcdc2a-9d94-4614-b654-0ce8160c978801024280c4b98110 --env production
```

---

## How to View Logs

```bash
# Stream live logs
az webapp log tail --resource-group project-pulse-rg --name project-pulse-api-tcu2026

# Download logs for inspection
az webapp log download --resource-group project-pulse-rg --name project-pulse-api-tcu2026 --log-file logs.zip
unzip -o logs.zip -d logs
cat logs/LogFiles/Application/*.log
```

---

## Known Behaviors

### Azure SQL Free Tier Auto-Pause
The free Azure SQL database **auto-pauses after inactivity**. The first request after a pause triggers a wake-up that takes 30-60 seconds. This means:
- First deployment takes ~3-5 minutes for the backend to start
- After periods of inactivity, the first API call may be slow
- The health check may show `DOWN` briefly while the DB wakes up

### Startup Time
The B1 App Service tier is slow. Spring Boot startup takes **2-3 minutes** on a cold start. This is normal for the cheapest tier.

### Mail
Email functionality (invitations, password resets) is **not configured for production**. The mail health check is disabled to prevent the `/actuator/health` endpoint from showing `DOWN`. To enable email in production, set these environment variables:
- `MAIL_HOST` — SMTP server (e.g., `smtp.gmail.com`)
- `MAIL_PORT` — SMTP port (e.g., `587`)
- `MAIL_USERNAME` — SMTP username
- `MAIL_PASSWORD` — SMTP password

---

## Estimated Monthly Cost

| Resource | Cost |
|----------|------|
| App Service B1 | ~$13/month |
| Azure SQL Free | $0 |
| Static Web Apps Free | $0 |
| **Total** | **~$13/month** |

Covered by Azure for Students $100 credit (~7 months of usage).

---

## From Scratch Setup (if you ever need to recreate everything)

### Prerequisites
- Azure CLI installed (`brew install azure-cli`)
- Node.js 18+
- Java 21+
- `@azure/static-web-apps-cli` installed globally (`npm install -g @azure/static-web-apps-cli`)

### Steps

```bash
# 1. Login
az login --tenant 6e2d7d24-bbe9-43f7-a31f-fd753eb4d4aa

# 2. Create resource group
az group create --name project-pulse-rg --location centralus

# 3. Create SQL Database — USE THE AZURE PORTAL UI
#    Go to portal.azure.com > SQL databases > Create > SQL database (Free offer)
#    - Resource group: project-pulse-rg
#    - Database name: project-pulse
#    - Server: create new (name: project-pulse-sql-tcu2026, admin: pulseadmin)
#    - Under Networking: enable "Allow Azure services" firewall rule

# 4. Register web provider
az provider register --namespace Microsoft.Web --wait

# 5. Create App Service plan
az appservice plan create --name project-pulse-plan --resource-group project-pulse-rg --sku B1 --is-linux --location centralus

# 6. Create web app
az webapp create --resource-group project-pulse-rg --plan project-pulse-plan --name project-pulse-api-tcu2026 --runtime "JAVA:21-java21"

# 7. Set environment variables
az webapp config appsettings set --resource-group project-pulse-rg --name project-pulse-api-tcu2026 --settings SPRING_PROFILES_ACTIVE=prod AZURE_SQL_USER=pulseadmin AZURE_SQL_PASSWORD='WebTech123' FRONTEND_URL=https://YOUR-FRONTEND-URL.azurestaticapps.net

# 8. Build and deploy backend
cd backend
./mvnw clean package -DskipTests
az webapp deploy --resource-group project-pulse-rg --name project-pulse-api-tcu2026 --src-path target/project-pulse-0.0.1-SNAPSHOT.jar --type jar

# 9. Create Static Web App
az staticwebapp create --name project-pulse-frontend --resource-group project-pulse-rg --location centralus

# 10. Build and deploy frontend
cd frontend
npm run build
az staticwebapp secrets list --name project-pulse-frontend --resource-group project-pulse-rg --query "properties.apiKey" -o tsv
swa deploy dist --deployment-token <TOKEN>
```

---

## Tear Down (delete everything)

```bash
az group delete --name project-pulse-rg --yes --no-wait
```

This deletes ALL Azure resources in the resource group. Cannot be undone.
