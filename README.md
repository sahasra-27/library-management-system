# 📚 Smart Library Book Management System

A full-stack, enterprise-grade library management platform built with a **Spring Boot 3.x** REST API backend, a responsive glassmorphic **React 19 + Vite 6** single-page application (SPA), and a **MySQL** database. Designed for institutional book inventory management, multi-role access control, online book reservations, copy-level barcode tracking, issue/return workflows, dynamic fine calculation, system audit logging, downloadable PDF/Excel reports, and real-time external book metadata fetching via the **Open Library REST API**.

---

## 🌐 Live Deployments & Repository

| Resource | URL / Link | Hosting / Platform |
| --- | --- | --- |
| **Live Web Application** | [https://library-management-system-sand-tau.vercel.app/](https://library-management-system-sand-tau.vercel.app/) | Vercel (React SPA) |
| **Production Backend API** | [https://library-management-system-production-3cc3.up.railway.app](https://library-management-system-production-3cc3.up.railway.app) | Railway (Spring Boot) |
| **Database Server** | Managed MySQL Instance | Railway MySQL |
| **Source Code Repository** | [https://github.com/sahasra-27/library-management-system](https://github.com/sahasra-27/library-management-system) | GitHub |

---

## 📌 Table of Contents

- [💡 Overview](#-overview)
- [🎯 Why This Project Exists](#-why-this-project-exists)
- [✨ Key Features](#-key-features)
- [👨‍💼 Admin Module](#-admin-module)
- [👨‍🎓 Member / User Module](#-member--user-module)
- [🏗️ Architecture Overview](#%EF%B8%8F-architecture-overview)
- [🛠️ Tech Stack](#%EF%B8%8F-tech-stack)
- [🔐 Authentication & Authorization](#-authentication--authorization)
- [🗄️ Database Design](#%EF%B8%8F-database-design)
- [⚙️ Local Development Setup](#%EF%B8%8F-local-development-setup)
- [⚡ Environment Variables](#-environment-variables)
- [📁 Project Structure](#-project-structure)
- [🔌 API Overview](#-api-overview)
- [🧪 Testing & Quality Assurance](#-testing--quality-assurance)
- [🚀 Deployment](#-deployment)
- [🖼️ Screenshots](#%EF%B8%8F-screenshots)
- [⚠️ Known Limitations](#%EF%B8%8F-known-limitations)
- [🗺️ Roadmap / Future Enhancements](#%EF%B8%8F-roadmap--future-enhancements)
- [👩‍💻 Author](#-author)
- [🔗 Project Links](#-project-links)

---

## 💡 Overview

The **Smart Library Book Management System** streamlines book cataloging, member management, and circulation workflows for modern educational institutions. The application provides two tailored portal interfaces:

1. **Administrator Portal**: Offers complete administrative authority to manage book inventories, physical copy barcodes (`BAR-...`), author/publisher/category registries, user account statuses (`ACTIVE`, `SUSPENDED`), book circulation (issues and returns), reservation fulfillment queues, fine payments, audit logging, and bulk catalog exports (PDF/Excel/CSV).
2. **Member / Student Portal**: Empowers users to search the library catalog in real time, view availability status, place online reservations, track active borrows and due dates, monitor fine histories, update personal profiles, and receive system notifications.

---

## 🎯 Why This Project Exists

Traditional library management often relies on manual ledger records or fragmented software, leading to misplaced inventory, untracked book damage, uncalculated late fees, and slow checkout queues. 

This project solves these operational challenges by providing:
- **Copy-Level Barcode Tracking**: Differentiates between catalog titles and individual physical book copies (`BookCopy`).
- **Automated Fine Calculation**: Dynamic fee calculation based on overdue days and configurable system daily rates.
- **External Catalog Auto-Fill**: Eliminates manual data entry by querying the Open Library API using an ISBN to auto-populate title, author, description, and cover artwork.
- **Auditable Security**: Comprehensive activity logging (`ActivityLog`) capturing user actions and administrative state modifications.

---

## ✨ Key Features

### 📦 Book Catalog & Inventory Management
- Full CRUD management for catalog titles with ISBNs, prices, editions, language, and shelf/rack locations.
- Physical copy tracking (`BookCopy`) with status indicators (`AVAILABLE`, `BORROWED`, `RESERVED`, `DAMAGED`, `LOST`).
- Bulk export catalog data to **Excel (.xlsx)** and **CSV**.
- Multipart file upload support for custom book cover images.

### 🔍 Open Library Integration
- Real-time book metadata fetch by ISBN via Open Library REST endpoints.
- Auto-fills book details (title, subtitle, page counts, cover artwork URLs, subject tags).
- Live book search across external Open Library catalog.

### 🔄 Issue & Return Circulation Engine
- Issue copies by scanning or entering unique copy barcodes (`BAR-...`).
- Automatic borrowing limit validation based on configurable maximum book limits.
- Outstanding fine check prevents new issues until existing balance is cleared.
- Return processing records return date, assesses late fees, and updates book copy condition notes.

### 📑 Reservation Queue System
- Online reservation requests for available or out-of-stock titles.
- Queue position tracking (`queuePosition`) for pending member reservations.
- Administrative reservation approval, fulfillment, or cancellation.

### 💰 Fine & Fee Management
- Automated overdue fine calculation.
- Fine tracking with status states (`UNPAID`, `PAID`, `WAIVED`).
- Payment processing and fee waiver logging.

### 📊 Dashboard & System Analytics
- Real-time statistical metrics for total books, total copies, active members, issued books, overdue books, and total collected fines.
- Visual category distribution charts powered by **Chart.js**.

### 📄 Reports & Audit Logs
- Generate downloadable **PDF reports** for inventory, circulation history, and fine summaries using OpenPDF.
- System activity audit logger (`ActivityLog`) recording user logins, circulation actions, and profile changes.

---

## 👨‍💼 Admin Module

The Admin interface (accessible to users with the `ADMIN` role) includes:
- **Dashboard (`/admin`)**: Interactive statistics widgets and category distribution charts.
- **Book Management (`/admin/books`)**: Catalog CRUD, ISBN Open Library search, cover image uploads, Excel/CSV bulk export, and CSV import.
- **Authors & Publishers (`/admin/authors`, `/admin/publishers`)**: Directory management for catalog authors and publisher metadata.
- **Categories (`/admin/categories`)**: Manage catalog classification categories.
- **User Directory (`/admin/users`)**: Member directory listing, role assignment (`ADMIN` / `USER`), account status toggling (`ACTIVE` / `SUSPENDED`), and profile updates.
- **Issue & Return Center (`/admin/issue-return`)**: Issue copies by barcode and process returns with condition notes.
- **Reservations (`/admin/reservations`)**: Manage and fulfill reservation queues.
- **Fines Management (`/admin/fines`)**: View outstanding member fines, process payments, or apply waivers.
- **Reports & Audit Logs (`/admin/reports`)**: Searchable audit logs and downloadable PDF reports.
- **System Settings (`/admin/settings`)**: Configure daily fine rates, maximum borrowing limits, and loan duration periods.

---

## 👨‍🎓 Member / User Module

The Member interface (accessible to users with the `USER` role) includes:
- **Member Dashboard (`/student`)**: Overview of active borrows, pending reservations, unpaid fines, and recent library notices.
- **Search Catalog (`/student/books`)**: Search catalog by title, author, category, or language with live availability status chips.
- **My Issued Books (`/student/issued`)**: Track currently borrowed books, issue dates, and due dates.
- **My Reservations (`/student/reservations`)**: View reservation queue status and cancel pending requests.
- **My Fines (`/student/fines`)**: View fine history, fee amounts, and payment statuses.
- **Notifications (`/student/notifications`)**: Member inbox with read/unread tracking for return reminders and queue updates.
- **My Profile (`/student/profile`)**: Manage personal details, update occupation/phone number, change password, and upload profile photo.

---

## 🏗️ Architecture Overview

```
                               ┌─────────────────────────────────────────┐
                               │           Web Browser Client            │
                               │  (React 19 SPA on Vercel Edge Network)  │
                               └────────────────────┬────────────────────┘
                                                    │
                                                    │ HTTPS / REST API (JSON)
                                                    ▼
                               ┌─────────────────────────────────────────┐
                               │       Spring Boot 3.2.5 REST API        │
                               │       (Hosted on Railway Platform)      │
                               └──────────┬──────────────────┬───────────┘
                                          │                  │
                          Spring Data JPA │                  │ RestTemplate HTTP
                           / HikariPool   ▼                  ▼
                    ┌──────────────────────────┐    ┌──────────────────────────┐
                    │    MySQL 8.0 Database    │    │  Open Library REST API   │
                    │   (Hosted on Railway)    │    │  (External Book Lookup)  │
                    └──────────────────────────┘    └──────────────────────────┘
```

### Request Flow Summary:
1. The **React SPA** interacts with Spring Boot REST controllers via dynamic Axios HTTP requests using JWT/session headers (`Authorization: Bearer mock-token-{id}-{ROLE}`).
2. **Spring Boot Controllers** delegate request parameters to transactional **Service classes** (`BookService`, `IssueService`, `UserService`, `OpenLibraryService`).
3. **Spring Data JPA Repositories** interact with the **MySQL Database** via HikariCP connection pooling.
4. For ISBN auto-fill queries, `OpenLibraryService` invokes external Open Library endpoints (`https://openlibrary.org/api/books`) and maps responses to DTOs.
5. Production SPA routing is handled by `frontend/vercel.json` rewrites, routing all path requests to `/index.html`.

---

## 🛠️ Tech Stack

| Domain | Technology | Version | Purpose |
| --- | --- | --- | --- |
| **Frontend Core** | React | `19.1.0` | UI Component Framework |
| **Build Tool** | Vite | `6.3.5` | Next-generation frontend bundler & dev server |
| **Routing** | React Router DOM | `7.18.1` | Client-side declarative routing |
| **UI Components** | Material UI (MUI) | `9.2.0` | Glassmorphic design design system & components |
| **Icons** | MUI Icons Material | `9.2.0` | Vector icon suite |
| **HTTP Client** | Axios | `1.18.1` | REST API client with request interceptors |
| **Forms & Validation**| React Hook Form | `7.81.0` | Form handling & validation |
| **Notifications** | React Toastify | `11.1.0` | Toast feedback notifications |
| **Data Visualization**| Chart.js + React-ChartJS-2 | `4.5.1` / `5.3.1` | Interactive dashboard charts |
| **Animations** | Framer Motion | `12.42.2` | Fluid UI transitions |
| **Backend Core** | Java | `17` / `21` | High-performance execution runtime |
| **Framework** | Spring Boot | `3.2.5` | Enterprise Java REST API framework |
| **Data Access** | Spring Data JPA / Hibernate | `6.4.4` | Object-Relational Mapping (ORM) |
| **Database Engine** | MySQL | `8.0` / `8.4` | Relational Database Management System |
| **Connection Pool** | HikariCP | Built-in | Database connection pooling |
| **PDF Generation** | OpenPDF | `1.3.30` | PDF report document creation |
| **Excel Export** | Apache POI | `5.2.5` | Excel (.xlsx) report generation |
| **API Documentation** | Springdoc OpenAPI / Swagger | `2.2.0` | OpenAPI documentation generator |
| **External API** | Open Library API | REST | External book metadata & cover lookup |

---

## 🔐 Authentication & Authorization

### Authentication Mechanism
- User login (`POST /api/auth/login`) validates credentials against the database.
- Upon successful authentication, the server returns a user payload containing user details (`id`, `username`, `email`, `role`, `occupation`) and a session token formatted as `mock-token-{id}-{ROLE}`.
- The React client stores session state (`user`, `role`, `token`) in `localStorage` managed via `AuthContext`.
- Axios request interceptors in `frontend/src/services/api.js` automatically append `Authorization: Bearer <token>` to outgoing requests.

### Role-Based Access Control (RBAC)
The application enforces two distinct roles:
- `ADMIN`: Full administrative access to management portals, reports, circulation, and user settings.
- `USER`: Member-level access restricted to catalog searching, self-reservations, personal borrow history, fines, and profile management.

### Protected Layout Guards
React Router paths are guarded by higher-order layout components:
- `AdminLayout`: Redirects non-admin users or unauthenticated sessions to `/login`.
- `StudentLayout`: Redirects unauthenticated sessions to `/login`.

---

## 🗄️ Database Design

The MySQL database `smart_library` consists of 14 relational domain entities:

| Entity Class | Database Table | Key Attributes | Relationships |
| --- | --- | --- | --- |
| `User` | `users` | `id`, `username`, `email`, `password`, `phone`, `role`, `status`, `occupation`, `profile_photo` | One-to-Many with `IssuedBook`, `Reservation`, `Fine` |
| `Book` | `books` | `id`, `title`, `subtitle`, `isbn`, `price`, `quantity`, `available_quantity`, `status` | Many-to-One with `Author`, `Publisher`, `Category`; One-to-Many with `BookCopy` |
| `BookCopy` | `book_copies` | `id`, `barcode`, `status` (`AVAILABLE`, `BORROWED`, `RESERVED`, `DAMAGED`, `LOST`) | Many-to-One with `Book` |
| `Author` | `authors` | `id`, `name`, `biography` | One-to-Many with `Book` |
| `Publisher` | `publishers` | `id`, `name`, `address`, `phone` | One-to-Many with `Book` |
| `Category` | `categories` | `id`, `name`, `description` | One-to-Many with `Book` |
| `IssuedBook` | `issued_books` | `id`, `issue_date`, `due_date`, `return_date`, `status` (`ISSUED`, `RETURNED`, `OVERDUE`), `fine_amount` | Many-to-One with `User`, `BookCopy` |
| `ReturnedBook` | `returned_books` | `id`, `return_date`, `fine_amount`, `book_condition`, `processed_by` | One-to-One with `IssuedBook` |
| `Reservation` | `reservations` | `id`, `reservation_date`, `status`, `queue_position`, `expiry_date` | Many-to-One with `User`, `Book` |
| `Fine` | `fines` | `id`, `amount`, `fine_date`, `status` (`UNPAID`, `PAID`, `WAIVED`), `paid_date` | Many-to-One with `User`, `IssuedBook` |
| `Notification` | `notifications` | `id`, `type`, `title`, `message`, `is_read` | Many-to-One with `User` |
| `ActivityLog` | `activity_logs` | `id`, `action`, `details`, `timestamp`, `ip_address` | Many-to-One with `User` |
| `Setting` | `settings` | `id`, `setting_key`, `setting_value`, `description` | Key-Value System Configurations |
| `BookImage` | `book_images` | `id`, `image_url`, `is_primary` | Many-to-One with `Book` |

---

## ⚙️ Local Development Setup

### Prerequisites
- **Java Development Kit (JDK 17 or JDK 21)**
- **Node.js (v18+) & npm**
- **MySQL Server (v8.0+)**

---

### Step 1: Database Setup
Start your local MySQL service and create the database:
```sql
CREATE DATABASE smart_library;
```

---

### Step 2: Backend Setup
1. Open a terminal and navigate to the `backend` folder:
   ```cmd
   cd backend
   ```
2. Copy the environment configuration template (optional):
   ```cmd
   copy .env.example .env
   ```
3. Compile and launch the Spring Boot application using the Maven wrapper:
   ```cmd
   mvnw.cmd spring-boot:run
   ```
   *The backend REST API will start on `http://localhost:8081`.*

---

### Step 3: Frontend Setup
1. Open a second terminal in the `frontend` folder:
   ```cmd
   cd frontend
   ```
2. Install dependencies:
   ```cmd
   npm install
   ```
3. Copy the frontend environment configuration template:
   ```cmd
   copy .env.example .env.local
   ```
4. Start the Vite development server:
   ```cmd
   npm run dev
   ```
5. Open your browser and navigate to [http://localhost:5173](http://localhost:5173).

---

## ⚡ Environment Variables

### Frontend Environment Variables (`frontend/.env.example`)

| Variable Name | Purpose | Safe Example Value |
| --- | --- | --- |
| `VITE_API_URL` | Base URL pointing to the backend REST API | `http://localhost:8081/api` |

### Backend Environment Variables (`backend/.env.example`)

| Variable Name | Purpose | Safe Example Value |
| --- | --- | --- |
| `PORT` | HTTP server port for Spring Boot | `8081` |
| `DB_HOST` | MySQL database host address | `localhost` |
| `DB_PORT` | MySQL server port | `3306` |
| `DB_NAME` | MySQL database schema name | `smart_library` |
| `DB_USERNAME` | MySQL database user | `root` |
| `DB_PASSWORD` | MySQL database password placeholder | `your_mysql_password` |
| `SPRING_DATASOURCE_URL` | Complete JDBC database connection string | `jdbc:mysql://localhost:3306/smart_library` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated list of allowed CORS origins | `*` |
| `MAIL_HOST` | SMTP server host for notifications | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP server port | `587` |
| `MAIL_USERNAME` | SMTP account email placeholder | `your_email@gmail.com` |
| `MAIL_PASSWORD` | SMTP app password placeholder | `your_app_password` |

---

## 📁 Project Structure

```
library-management-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smartlibrary/
│   │   │   │   ├── configuration/   # WebMvc, CORS & upload resource handlers
│   │   │   │   ├── controller/      # REST Endpoints (Auth, Books, Issues, Fines, etc.)
│   │   │   │   ├── dto/             # Request & Response Data Transfer Objects
│   │   │   │   ├── entity/          # JPA Domain Entities (User, Book, Issue, etc.)
│   │   │   │   ├── exception/       # Custom Exception Handler (@ControllerAdvice)
│   │   │   │   ├── mapper/          # DTO <-> Entity Mappers
│   │   │   │   ├── repository/      # Spring Data JPA Interface Repositories
│   │   │   │   └── service/         # Transactional Business Logic Services
│   │   │   └── resources/
│   │   │       ├── application.properties  # Configurable environment properties
│   │   │       └── import.sql       # Seed data for default admin, users & categories
│   ├── .env.example                 # Backend environment variable template
│   ├── .gitignore                   # Ignored build outputs (target/, .env, logs)
│   └── pom.xml                      # Maven build definition & dependencies
│
├── frontend/
│   ├── src/
│   │   ├── components/              # Layout, Navbar, Sidebar UI components
│   │   ├── context/                 # AuthContext provider
│   │   ├── layouts/                 # Protected Admin and Student route guards
│   │   ├── pages/
│   │   │   ├── admin/               # Admin pages (Dashboard, Books, Fines, Reports, etc.)
│   │   │   └── student/             # Member pages (SearchCatalog, MyIssued, Profile, etc.)
│   │   ├── services/                # Axios instance (`api.js`) & URL resolvers
│   │   ├── App.jsx                  # Main application router
│   │   └── main.jsx                 # Vite application entry
│   ├── .env.example                 # Frontend environment variable template
│   ├── .gitignore                   # Ignored SPA build outputs (dist/, node_modules)
│   ├── package.json                 # Frontend dependencies & scripts
│   ├── vercel.json                  # SPA rewrite configuration for Vercel deployment
│   └── vite.config.js               # Vite build configuration
│
├── .gitignore                       # Root Git ignore rules
└── README.md                        # Master repository documentation
```

---

## 🔌 API Overview

| Module | Endpoint | Method | Description |
| --- | --- | --- | --- |
| **Auth** | `/api/auth/login` | `POST` | Authenticate user & return user payload with token |
| **Auth** | `/api/auth/register` | `POST` | Register a new member account |
| **Auth** | `/api/auth/forgotpassword` | `POST` | Request password reset instructions |
| **Auth** | `/api/auth/resetpassword` | `POST` | Reset password using reset token |
| **Auth** | `/api/auth/logout` | `POST` | Logout session |
| **Books** | `/api/books` | `GET` | Fetch paginated catalog with query/category filters |
| **Books** | `/api/books/{id}` | `GET` | Retrieve book details by ID |
| **Books** | `/api/books` | `POST` | Create new catalog title (supports multipart file cover upload) |
| **Books** | `/api/books/{id}` | `PUT` | Update book title details |
| **Books** | `/api/books/{id}` | `DELETE` | Mark book status as `DELETED` |
| **Books** | `/api/books/isbn/{isbn}` | `GET` | Search Open Library by ISBN |
| **Books** | `/api/books/export/excel` | `GET` | Export catalog to Excel (.xlsx) |
| **Books** | `/api/books/export/csv` | `GET` | Export catalog to CSV |
| **Authors** | `/api/authors` | `GET` / `POST` | List and create book authors |
| **Publishers**| `/api/publishers` | `GET` / `POST` | List and create publishers |
| **Categories**| `/api/categories` | `GET` / `POST` | List and create book categories |
| **Users** | `/api/users` | `GET` / `PUT` | List users and update user profile details |
| **Users** | `/api/users/{id}/status` | `PATCH` | Update account status (`ACTIVE` / `SUSPENDED`) |
| **Issues** | `/api/issues` | `GET` / `POST` | Search issued list & issue book copy by barcode |
| **Issues** | `/api/issues/{id}/renew` | `POST` | Renew loan due date |
| **Returns** | `/api/returns` | `GET` / `POST` | List returns and process book return with condition notes |
| **Reservations**|`/api/reservations` | `GET` / `POST` / `DELETE` | Manage member reservation requests |
| **Fines** | `/api/fines` | `GET` | List fine records |
| **Fines** | `/api/fines/{id}/pay` | `POST` | Process fine payment |
| **Fines** | `/api/fines/{id}/waive` | `POST` | Apply fine waiver |
| **Notifications**|`/api/notifications` | `GET` / `POST` | Retrieve user notification inbox |
| **Dashboard**| `/api/dashboard` | `GET` | Retrieve summary dashboard statistics |
| **Reports** | `/api/reports/pdf/{type}` | `GET` | Download PDF report (books, issues, fines) |
| **Reports** | `/api/reports/audit-logs` | `GET` | Fetch searchable activity audit logs |
| **OpenLibrary**|`/api/openlibrary/search` | `GET` | Query external Open Library API catalog |

---

## 🧪 Testing & Quality Assurance

### Automated Build & Compilation Verification
- **Backend Clean Compilation**: Verified using `.\mvnw.cmd clean compile` (compiles all 86 Java source files with **0 errors**).
- **Frontend SPA Production Build**: Verified using `npm run build` (builds production minified bundle in `dist/` with **0 errors**).
- **Backend Context Load Test**: Spring Boot application context load test (`SmartLibraryApplicationTests.java`) passes cleanly.

### End-to-End API Integration Suite
Executed comprehensive integration tests verifying:
- **Authentication**: Admin (`Users`) and Member (`mamtha`) authentication flows.
- **Catalog Operations**: Book creation, category filtering, search queries, and ISBN lookups.
- **Circulation Workflows**: Copy-level issuing by barcode, return processing, and reservation queue cancellations.

---

## 🚀 Deployment

The system is deployed in a cloud production environment across Vercel and Railway:

```
┌───────────────────────────────────────────────────────────┐
│                      Vercel Platform                      │
│     Hosts React 19 Frontend SPA (Vite Production Build)   │
│   URL: https://library-management-system-sand-tau.vercel.app │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              │ HTTPS REST Requests
                              ▼
┌───────────────────────────────────────────────────────────┐
│                     Railway Platform                      │
│           Hosts Spring Boot 3.2.5 Java Backend            │
│  URL: https://library-management-system-production-3cc3   │
│                          .up.railway.app                  │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              │ Private JDBC Connection
                              ▼
┌───────────────────────────────────────────────────────────┐
│                    Railway MySQL Instance                 │
│              Hosts Database Engine & Seed Data            │
└───────────────────────────────────────────────────────────┘
```

### Key Deployment Configurations:
1. **Frontend SPA Routing (`frontend/vercel.json`)**:
   Contains URL rewrite rules routing all client paths to `/index.html` to prevent 404 errors when refreshing React Router routes:
   ```json
   {
     "rewrites": [
       {
         "source": "/(.*)",
         "destination": "/index.html"
       }
     ]
   }
   ```
2. **Dynamic API Endpoint Override**: The production frontend relies on `VITE_API_URL=https://library-management-system-production-3cc3.up.railway.app/api`.
3. **Backend Cloud Port Override**: The Spring Boot backend dynamically binds to the `PORT` environment variable assigned by Railway.

---

## 🖼️ Screenshots

*(Add application UI screenshots below after taking high-resolution captures)*

```
+-------------------------------------------------------------------+
|                        Admin Dashboard UI                         |
|   [ Total Books: 14 ]  [ Active Members: 7 ]  [ Issued: 2 ]       |
|   (Visual category distribution charts & system quick stats)      |
+-------------------------------------------------------------------+
```

- **Admin Dashboard**: `docs/screenshots/admin_dashboard.png`
- **Book Catalog**: `docs/screenshots/book_catalog.png`
- **Issue & Return Center**: `docs/screenshots/issue_return.png`
- **Member Portal**: `docs/screenshots/member_portal.png`

---

## ⚠️ Known Limitations

1. **Authentication Token Format**: Session authentication uses synthetic session tokens (`mock-token-{id}-{ROLE}`). Future releases will integrate standard JWT signed token validation (`JwtAuthenticationFilter`).
2. **Password Hashing**: Seeded local development passwords in `import.sql` are in plain text. Production environments should integrate `BCryptPasswordEncoder` hashing before processing real user production credentials.

---

## 🗺️ Roadmap / Future Enhancements

### Implemented Features
- [x] Spring Boot 3 REST API backend with MySQL integration.
- [x] React 19 glassmorphic frontend UI with Material UI.
- [x] Copy-level barcode tracking (`BookCopy`) and circulation workflows.
- [x] Open Library API ISBN metadata search and auto-fill.
- [x] Dynamic fine calculation and waiver handling.
- [x] Downloadable PDF, Excel (.xlsx), and CSV catalog reports.
- [x] SPA URL rewrites for Vercel deployment (`vercel.json`).

### Planned Enhancements
- [ ] Integration of signed JSON Web Tokens (JWT) with Spring Security filter chain.
- [ ] Integration of BCrypt password hashing.
- [ ] Camera-based barcode scanner integration for quick copy checkout.
- [ ] Automated SMS notification reminders via Twilio integration.

---

## 👩‍💻 Author

**Sahasra**  
*B.Tech Computer Science / IoT Student*  
GitHub: [https://github.com/sahasra-27](https://github.com/sahasra-27)

---

## 🔗 Project Links

- **Live Application**: [https://library-management-system-sand-tau.vercel.app/](https://library-management-system-sand-tau.vercel.app/)
- **Production REST API**: [https://library-management-system-production-3cc3.up.railway.app](https://library-management-system-production-3cc3.up.railway.app)
- **GitHub Repository**: [https://github.com/sahasra-27/library-management-system](https://github.com/sahasra-27/library-management-system)
