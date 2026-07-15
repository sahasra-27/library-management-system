# Smart Library Book Management System

A production-ready enterprise-grade library management application built with a **Spring Boot 3.x** REST API backend and a responsive glassmorphic **React + Vite** frontend.

---

## Project Folder Structure

```
c:/Users/Admin/Desktop/library
├── backend
│   ├── pom.xml
│   ├── src
│   │   ├── main
│   │   │   ├── java/com/smartlibrary
│   │   │   │   ├── configuration/      # Web MVC and resource servers configurations
│   │   │   │   ├── controller/         # REST Controllers (Auth, Book, Issues, Fines)
│   │   │   │   ├── dto/                # Request/Response data transfer objects
│   │   │   │   ├── entity/             # 17 JPA mapping models
│   │   │   │   ├── exception/          # Custom exceptions & Global Advice Handler
│   │   │   │   ├── mapper/             # Static entity-DTO mappers
│   │   │   │   ├── repository/         # Data JPA repository queries
│   │   │   │   ├── security/           # JWT Filters, UserDetailsService & Configs
│   │   │   │   └── service/            # Core business workflows
│   │   │   └── resources
│   │   │       ├── application.properties
│   │   │       └── import.sql          # Auto-populates sample records on startup
│   └── uploads/                        # Local file uploads storage directory
└── frontend
    ├── package.json
    ├── vite.config.js
    ├── dist/                           # Compiled production SPA assets
    └── src
        ├── main.jsx
        ├── App.jsx                     # Routing configuration
        ├── index.css                   # Global font layouts
        ├── components/                 # Navbar, Sidebar, Footer components
        ├── context/                    # AuthContext, ThemeContext
        ├── layouts/                    # AdminLayout, StudentLayout guards
        ├── pages/                      # Dashboard, Books, Profile modules
        └── services/                   # Axios API intercepts
```

---

## Setup & Running Instructions

### Prerequisites
1. **Java Development Kit (JDK 17)** or above.
2. **Node.js (v18+)** and **npm** package manager.
3. **MySQL Server 8.0** running locally (or Aiven MySQL connection details).

---

### Step 1: Backend Database Configuration
The application is pre-configured to connect to MySQL database named `smart_library` on port `3306`.
If your MySQL password is not `root`, modify the password field in:
[backend/src/main/resources/application.properties](file:///c:/Users/Admin/Desktop/library/backend/src/main/resources/application.properties):
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

*Note: Hibernate will automatically create all tables and relationships on startup. The file `import.sql` will automatically seed roles, users (`admin`/`student`), settings, and category records.*

---

### Step 2: Build & Start the Backend
Open a terminal in the `/backend` folder and run the Maven wrapper to compile and start the server:
```bash
cd backend
mvnw spring-boot:run
```
The REST API will launch at `http://localhost:8080`.

---

### Step 3: Run the Frontend
Open a new terminal in the `/frontend` folder and run:
```bash
cd frontend
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your web browser.

---

## Default Login Credentials (Seeded)

- **Admin User**:
  - Username: `admin`
  - Password: `password`
- **Student/User**:
  - Username: `student`
  - Password: `password`

---

## API Testing & Postman

The Postman testing collection JSON is exported in the scratch directory here:
[postman_collection.json](file:///C:/Users/Admin/.gemini/antigravity-ide/scratch/postman_collection.json)

You can import this file directly into Postman to run automated request suites against `/api/auth/login`, `/api/auth/register`, and book query parameters.
