# Tourmate

**Tourmate** is a web application for managing travel preferences. Users can register, log in, and save their preferences.  

- **Backend**: Spring Boot with Spring Security and JPA  
- **Frontend**: Angular 20  

---

## Features

- User registration and login with hashed passwords (BCrypt)  
- Session management with Spring Security  
- Create, update, and view travel preferences  
- Interactive frontend with preference selection toggling  
- System notifications for success/error messages  
- RESTful API with `/api` endpoints  

---

## Requirements

- **Java** 17+  
- **Maven** 3+  
- **Node.js** 18+ / **npm** 9+  
- **Angular CLI** 20+  
- **Database**: MySQL / PostgreSQL (or any database supported by Spring JPA)  

---

## Quick Start

### Backend Setup

1. Configure your database in `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tourmate
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

2. Build and run the backend:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

> Backend API is available at: [http://localhost:8080/api](http://localhost:8080/api)

### Frontend Setup

1. Configure Angular proxy (`proxy.conf.json`) to route API requests to the backend:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

2. Install dependencies and run the frontend:

```bash
cd frontend
npm install
ng serve --proxy-config proxy.conf.json
```

> Frontend is accessible at: [http://localhost:4200](http://localhost:4200)

---

## API Endpoints

| Endpoint        | Method | Description                     |
|-----------------|--------|---------------------------------|
| `/auth/register`  | POST   | Register a new user             |
| `/auth/login`     | POST   | Log in and create session       |
| `/auth/logout`    | POST   | Log out and invalidate session  |
| `/auth/me`        | GET    | Get current logged-in user      |
| `/preferences`    | GET    | Get current user preferences    |
| `/preferences`    | POST   | Create new preferences          |
| `/preferences`    | PUT    | Update existing preferences     |

> **Note:** All `/preferences/**` endpoints require authentication.

---

## Project Structure

```
backend/           # Spring Boot backend
  ├─ src/main/java/com/tourmate
  │   ├─ controller/   # API controllers
  │   ├─ service/      # Business logic
  │   ├─ repository/   # JPA repositories
  │   ├─ entity/       # Entities (UserAccount, Preference)
  │   └─ config/       # Security & CORS config
  └─ src/main/resources
      └─ application.properties

tourmate_frontend/  # Angular frontend
  ├─ src/app
  │   ├─ services/        # API and auth services
  │   ├─ components/      # UI components
  │   └─ shared/          # Notifications, constants
  └─ proxy.conf.json
```

---

## Security

- **Authentication:** Session-based via Spring Security  
- **Password hashing:** BCryptPasswordEncoder  
- **Authorization:** `/preferences/**` endpoints require authentication  
- **CORS:** Configured to allow frontend access during development  

---

## Usage Notes

- Ensure the backend is running before the frontend  
- System notifications display errors such as invalid login or failed preference saving  
- Session is stored using **JSESSIONID** cookie; frontend maintains user state via `AuthService`  


