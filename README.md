# Smart Library Book Management System

A production-ready enterprise-grade library management application built with a **Spring Boot 3.x** REST API backend, a responsive glassmorphic **React + Vite** frontend, and a **MySQL** database. Designed for institutional book inventory tracking, user management, online reservations, issue/return workflows, fine management, automated email notifications, system audit logs, and external book metadata lookup via the Open Library REST API.

---

## Features

### Admin Module
- **Dashboard & Analytics**: Real-time stats on total catalog books, active users, issued copies, overdue items, total collected fines, and category distributions.
- **Book Management**: Complete CRUD operations for catalog books, copy tracking with unique barcodes (`BAR-...`), cover image uploads, and bulk Excel/CSV import/export.
- **Authors & Publishers**: Add, edit, search, and manage author biographies and publisher directories.
- **Categories**: Dynamic category management for organized book classification.
- **User Management**: View member accounts, manage user roles (`ADMIN`, `USER`), activate/suspend accounts, and track member history.
- **Issue & Return Workflows**: Issue available copies by barcode, process returns, calculate late fees dynamically, and record book condition notes.
- **Reservations Management**: Review, approve, reject, or cancel member reservation requests.
- **Fine Management**: View fine history, process fee payments, and record waived amounts.
- **Reports & System Audit**: Audit log viewer for security tracking and downloadable PDF system reports.
- **Open Library Metadata Import**: Automatic metadata and cover fetching by ISBN via Open Library REST API.

### Member / Student Module
- **Interactive Search Catalog**: Search books by title, author, category, or language with pagination and status chips.
- **Book Reservations**: Reserve out-of-stock or available books online with queue tracking.
- **Borrowing History**: View currently issued books, due dates, and past return records.
- **Fine Tracker**: View outstanding fines and payment history.
- **Notifications**: Personal notification inbox with read/unread tracking for due date reminders and reservation updates.
- **User Profile**: Edit personal profile details, change password, and upload profile photo.

---

## Technology Stack

### Frontend
- **Framework**: React 19 + Vite 6
- **Routing**: React Router DOM v7
- **UI Components & Styling**: Material UI (MUI v6/v9) + Emotion
- **State Management & Forms**: React Context API (`AuthContext`), React Hook Form
- **HTTP Client**: Axios with request/response interceptors
- **Notifications**: React Toastify
- **Charts & Visualizations**: Chart.js + React-ChartJS-2
- **Animations**: Framer Motion

### Backend
- **Framework**: Java 17/21 + Spring Boot 3.2.5
- **Data Access**: Spring Data JPA + Hibernate ORM 6.4
- **Security & Validation**: Spring Boot Starter Validation + Token Interceptor
- **Build Tool**: Apache Maven (Maven Wrapper `mvnw`)
- **Document Generation**: Apache POI (Excel) + OpenPDF (PDF exports)
- **API Documentation**: Springdoc OpenAPI / Swagger UI 2.2

### Database
- **Database Engine**: MySQL Server 8.0 / 8.4
- **Connection Pool**: HikariCP

### External Integrations
- **Open Library REST API**: Real-time book search and ISBN metadata auto-fill.

---

## System Architecture

```
┌────────────────────────────────────────────────────────┐
│               React + Vite Single-Page App             │
│            (MUI, Axios, Context API, Router)           │
└───────────────────────────┬────────────────────────────┘
                            │ HTTP / REST API (JSON)
                            ▼
┌────────────────────────────────────────────────────────┐
│                  Spring Boot 3 REST API                │
│    (Controllers, Services, Repositories, JPA Mappers)  │
└──────────────┬──────────────────────────┬──────────────┘
               │                          │
               ▼ JDBC                     ▼ HTTP REST
┌──────────────────────────────┐ ┌───────────────────────┐
│     MySQL 8.0 Database       │ │   Open Library API    │
│  (17 Relational Entities)    │ │  (Metadata & Covers)  │
└──────────────────────────────┘ └───────────────────────┘
```

---

## Project Structure

```
library/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smartlibrary/
│   │   │   │   ├── configuration/   # Web MVC & Resource handlers
│   │   │   │   ├── controller/      # REST Controllers (Auth, Book, Issues, Fines, etc.)
│   │   │   │   ├── dto/             # Data Transfer Objects & Validation
│   │   │   │   ├── entity/          # JPA Domain Entities
│   │   │   │   ├── exception/       # Global Exception Handling
│   │   │   │   ├── mapper/          # Entity-DTO Mappers
│   │   │   │   ├── repository/      # Spring Data JPA Repositories
│   │   │   │   └── service/         # Core Business Logic Services
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── import.sql       # Seed data for roles, admin & sample records
│   ├── .env.example                 # Template for backend environment variables
│   └── pom.xml                      # Maven project definition
│
├── frontend/
│   ├── src/
│   │   ├── components/              # Layout, Navbar, Sidebar components
│   │   ├── context/                 # AuthContext
│   │   ├── layouts/                 # Protected Admin and Student layout guards
│   │   ├── pages/                   # Admin and Student module views
│   │   ├── services/                # Axios instance (`api.js`)
│   │   ├── App.jsx                  # Main application router
│   │   └── main.jsx                 # Entry point
│   ├── .env.example                 # Template for frontend environment variables
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

---

## API Overview

| Group | Endpoint | Method | Description |
| --- | --- | --- | --- |
| **Auth** | `/api/auth/login` | POST | Authenticate user and return session token |
| **Auth** | `/api/auth/register` | POST | Register new member account |
| **Books** | `/api/books` | GET | Fetch paginated book catalog with search filters |
| **Books** | `/api/books/{id}` | GET | Get detailed book record by ID |
| **Books** | `/api/books` | POST | Create new book record (supports file upload) |
| **Books** | `/api/books/export/excel` | GET | Export catalog to Excel (.xlsx) |
| **Books** | `/api/books/export/csv` | GET | Export catalog to CSV |
| **Authors** | `/api/authors` | GET / POST | Manage book authors |
| **Publishers** | `/api/publishers` | GET / POST | Manage publishers |
| **Categories** | `/api/categories` | GET / POST | Manage categories |
| **Users** | `/api/users` | GET / PUT | Manage member accounts and update profiles |
| **Issues** | `/api/issues` | GET / POST | Issue book copy by barcode to member |
| **Returns** | `/api/returns` | GET / POST | Process book return and update availability |
| **Reservations**| `/api/reservations` | GET / POST / DELETE | Online book reservation queue management |
| **Fines** | `/api/fines` | GET / POST | View fines and process payments |
| **Notifications**| `/api/notifications` | GET / POST | Member notification inbox |
| **Reports** | `/api/reports/pdf/{type}`| GET | Download PDF reports (books, issues, fines) |
| **Open Library**| `/api/openlibrary/search`| GET | Query external Open Library catalog |

---

## Database Entities

- `User`: Member accounts, credentials, role (`ADMIN`, `USER`), status (`ACTIVE`, `SUSPENDED`).
- `Book`: Title, ISBN, price, quantity, available quantity, edition, rack/shelf numbers.
- `BookCopy`: Physical copy tracking with unique barcode (`BAR-...`) and status (`AVAILABLE`, `BORROWED`, `LOST`).
- `Author`, `Publisher`, `Category`: Catalog metadata entities.
- `IssuedBook`: Active and historical borrowing transactions with due dates.
- `ReturnedBook`: Recorded return dates, condition notes, and fine assessments.
- `Reservation`: Reservation queue with status (`PENDING`, `FULFILLED`, `CANCELLED`).
- `Fine`: Penalty records, amounts, and payment status (`UNPAID`, `PAID`, `WAIVED`).
- `Notification`: System alerts and inbox messages.
- `ActivityLog`: System audit logging for administrative actions.

---

## Local Installation & Setup

### Prerequisites
- **JDK 17 or JDK 21**
- **Node.js v18+ & npm**
- **MySQL Server 8.0+**

### Step 1: Database Setup
Create a MySQL database named `smart_library`:
```sql
CREATE DATABASE smart_library;
```

### Step 2: Backend Setup
1. Navigate to the `backend` directory:
   ```cmd
   cd backend
   ```
2. Copy environment template (optional):
   ```cmd
   copy .env.example .env
   ```
3. Run the Spring Boot application:
   ```cmd
   mvnw.cmd spring-boot:run
   ```
   *The REST API will launch on `http://localhost:8081`.*

### Step 3: Frontend Setup
1. Open a new terminal in the `frontend` directory:
   ```cmd
   cd frontend
   ```
2. Install dependencies:
   ```cmd
   npm install
   ```
3. Start Vite development server:
   ```cmd
   npm run dev
   ```
4. Access the web app at [http://localhost:5173](http://localhost:5173).

---

## Environment Variables

### Frontend Environment Variables (`frontend/.env.example`)
| Variable | Default Value (Local) | Description |
| --- | --- | --- |
| `VITE_API_URL` | `http://localhost:8081/api` | Backend REST API URL endpoint |

### Backend Environment Variables (`backend/.env.example`)
| Variable | Default Value (Local) | Description |
| --- | --- | --- |
| `PORT` | `8081` | Server port |
| `DB_HOST` | `localhost` | MySQL server host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `smart_library` | Database name |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `Sahasra$27` | MySQL password |
| `CORS_ALLOWED_ORIGINS` | `*` | Allowed CORS origins for production |

---

## Production Cloud Deployment Architecture

The application is structured for cloud deployment:

- **Frontend**: Deploy `frontend` build to **Vercel** / **Netlify** / **Render**. Set `VITE_API_URL=https://YOUR-BACKEND-URL.up.railway.app/api`.
- **Backend**: Deploy `backend` Maven app to **Railway** / **Render**. Set `PORT`, `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`.
- **Database**: Host MySQL database on **Railway MySQL** / **Aiven MySQL** / **AWS RDS**.

---

## Testing & Quality Assurance

- **Backend Integration Tests**: Verified REST endpoints for Auth, Books, Authors, Publishers, Categories, Users, Issues, Returns, Reservations, Fines, Notifications, Reports, and Open Library APIs.
- **Build Verification**: Clean compilation using `mvnw clean compile` (0 javac errors) and clean SPA build using `vite build` (0 bundle errors).

---

## Screenshots Placeholder

*(Add UI screenshots here after deploying or recording demo walkthroughs)*

- **Admin Dashboard**: `docs/screenshots/admin_dashboard.png`
- **Book Catalog**: `docs/screenshots/book_catalog.png`
- **Issue & Return**: `docs/screenshots/issue_return.png`

---

## Known Limitations

1. **Authentication Token Implementation**: Session authentication currently utilizes mock session tokens (`mock-token-{id}-{ROLE}`). Production deployments should incorporate standard signed JWT token validation (JSON Web Tokens with secret key validation).
2. **Password Storage**: Seeded local development passwords are in plain text. Production environments should integrate BCrypt password encoding via `PasswordEncoder`.

---

## Future Enhancements

- Integration of JWT security filter chain (`JwtAuthenticationFilter`).
- BCrypt password hashing.
- Integration of barcode scanner SDK for instant copy scanning.
- Automated SMS notifications via Twilio API.

---

## Author

**Sahasra**  
GitHub: [https://github.com/sahasra-27](https://github.com/sahasra-27)
