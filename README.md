# Project Pulse

A full-stack peer evaluation and project management platform built for academic team-based courses. Instructors can manage sections, teams, rubrics, and view peer evaluation reports. Students can log weekly activities and submit peer evaluations for their teammates.

---

## Prerequisites

Make sure you have the following installed on your machine:

| Tool       | Version   | Download                                      |
|------------|-----------|-----------------------------------------------|
| Java       | 21+       | https://adoptium.net/                         |
| Node.js    | 18+       | https://nodejs.org/                           |
| Docker     | 20+       | https://www.docker.com/products/docker-desktop|
| Maven      | 3.9+      | Included via `./mvnw` wrapper in backend      |

---

## Project Structure

```
project-pulse-recreation/
├── backend/                # Spring Boot 3.4.1 (Java 21)
│   ├── src/main/java/team/projectpulse/
│   │   ├── activity/       # Weekly Activity Reports (WAR)
│   │   ├── course/         # Course management
│   │   ├── evaluation/     # Peer evaluations & ratings
│   │   ├── instructor/     # Instructor entity & endpoints
│   │   ├── rubric/         # Rubrics & criteria
│   │   ├── section/        # Section management
│   │   ├── security/       # JWT auth, CORS, user details
│   │   ├── student/        # Student entity & endpoints
│   │   ├── system/         # Result envelope, exceptions, config
│   │   ├── team/           # Team management
│   │   └── user/           # User accounts, invitations, password reset
│   └── src/main/resources/
│       ├── application.yml
│       └── application-dev.yml
├── frontend/               # Vue 3 + Vite + Element Plus (JavaScript)
│   ├── src/
│   │   ├── apis/           # Axios API modules
│   │   ├── components/     # Dashboard layout
│   │   ├── pages/          # All page components
│   │   │   ├── admin/      # Admin/instructor management pages
│   │   │   ├── auth/       # Login, register, password reset
│   │   │   ├── instructor/ # Instructor-specific views
│   │   │   └── student/    # Student-specific views
│   │   ├── router/         # Vue Router with role-based guards
│   │   ├── stores/         # Pinia stores (token, userInfo, settings)
│   │   └── utils/          # Axios instance with interceptors
│   └── vite.config.js
├── docker-compose.yml      # MySQL 8 + Mailpit
└── README.md
```

---

## Getting Started

### Step 1: Start Docker Services

From the project root, start MySQL and Mailpit:

```bash
docker compose up -d
```

This starts:
- **MySQL 8** on port `3306` (database: `project-pulse`, user: `root`, password: `123456`)
- **Mailpit** — SMTP on port `1025`, Web UI on port `8025`

Verify they're running:

```bash
docker compose ps
```

### Step 2: Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

> **Windows users:** Use `mvnw.cmd spring-boot:run` instead.

The backend starts on **http://localhost** (port 80).

On first run with the `dev` profile (default), Hibernate will auto-create all database tables and the `DataInitializer` will seed sample data including users, a course, sections, teams, rubrics, students, and sample activities/evaluations.

### Step 3: Start the Frontend

Open a **new terminal**:

```bash
cd frontend
npm install --legacy-peer-deps
npm run dev
```

The frontend starts on **http://localhost:5173**.

> The `--legacy-peer-deps` flag is needed to resolve some peer dependency conflicts.

---

## Default Login Credentials

The dev data initializer creates the following accounts:

| Role       | Email               | Password |
|------------|---------------------|----------|
| Admin      | b.wei@abc.edu       | 123456   |
| Instructor | s.johnson@abc.edu   | 123456   |
| Student    | j.smith@abc.edu     | 123456   |
| Student    | e.davis@abc.edu     | 123456   |
| Student    | m.brown@abc.edu     | 123456   |
| Student    | s.wilson@abc.edu    | 123456   |
| Student    | d.taylor@abc.edu    | 123456   |
| Student    | l.anderson@abc.edu  | 123456   |

### Production Login (Azure)

Production is seeded with the same accounts as dev:

| Role       | Email               | Password |
|------------|---------------------|----------|
| Admin      | b.wei@abc.edu       | 123456   |
| Instructor | s.johnson@abc.edu   | 123456   |
| Student    | j.smith@abc.edu     | 123456   |
| Student    | e.davis@abc.edu     | 123456   |
| Student    | m.brown@abc.edu     | 123456   |
| Student    | s.wilson@abc.edu    | 123456   |
| Student    | d.taylor@abc.edu    | 123456   |
| Student    | l.anderson@abc.edu  | 123456   |

> See `docs/azure-deployment-guide.md` for full Azure deployment details.

---

## Viewing Emails Locally

When invitations or password reset emails are sent, they are captured by **Mailpit** (not actually delivered).

Open the Mailpit web UI at: **http://localhost:8025**

---

## Key Features

### Admin / Instructor
- Create and manage **courses**, **sections**, and **teams**
- Configure **rubrics** with custom evaluation criteria
- **Invite students and instructors** via email
- **Assign/unassign** students and instructors to teams
- Set **active weeks** for sections
- View **section-wide peer evaluation reports** with per-criterion averages
- Drill into **individual student evaluation reports** (includes private comments)
- View **team and student activity logs**
- **Activate/deactivate** instructor accounts

### Student
- Log **weekly activities** (category, description, planned/actual hours, status)
- **Submit peer evaluations** for teammates using the section's rubric
- View **personal evaluation report** with averaged scores and public comments

### Authentication
- **Email invitation-based registration** with token validation
- **JWT authentication** (2-hour token expiry)
- **Password reset** via email link
- **Role-based access control** (admin > instructor > student hierarchy)

---

## API Overview

All API endpoints are prefixed with `/api/v1`. The backend uses a standard response envelope:

```json
{
  "flag": true,
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

Authentication:
- **Login:** `POST /api/v1/users/login` (HTTP Basic Auth)
- **All other endpoints:** Bearer JWT token in `Authorization` header

---

## Tech Stack

| Layer    | Technology                                      |
|----------|-------------------------------------------------|
| Backend  | Spring Boot 3.4.1, Spring Security, Spring Data JPA, Java 21 |
| Frontend | Vue 3, Vite 6, Element Plus, Pinia, Axios, Chart.js |
| Database | MySQL 8                                         |
| Email    | Spring Mail + Mailpit (dev)                     |
| Auth     | JWT (RSA-signed) via Spring OAuth2 Resource Server |

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `Port 80 already in use` | Stop any process on port 80, or change the port in `backend/src/main/resources/application.yml` |
| `Port 3306 already in use` | Stop any local MySQL instance, or change the port in `docker-compose.yml` and `application-dev.yml` |
| `npm install` fails with peer dependency errors | Use `npm install --legacy-peer-deps` |
| Backend can't connect to MySQL | Make sure Docker containers are running: `docker compose ps` |
| Login returns 401 | Double-check email and password; the dev seed data uses the credentials listed above |
| Emails not showing up | Check Mailpit at http://localhost:8025 — emails are captured there, not sent externally |
