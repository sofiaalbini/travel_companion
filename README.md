# Tourmate

**Tourmate** is a web application for managing travel preferences. Users can register, log in, and save their preferences. The backend is implemented in **Spring Boot** with **Spring Security** and **JPA**, while the frontend is built in **Angular 20**.

---

## Features

- User registration and login with hashed passwords (BCrypt).  
- Session management with Spring Security.  
- Create, update, and view travel preferences.  
- Frontend displays available preferences and supports selection toggling.  
- System notifications for success/error messages.  
- RESTful API with `/api` endpoints.  

---

## Requirements

- Java 17+  
- Maven 3+  
- Node.js 18+ / npm 9+  
- Angular CLI 20+  
- MySQL / PostgreSQL (or any database supported by Spring JPA)  

---

## Backend Setup

1. Enter your database configuration in `backend/src/main/resources/application.properties`:

```bash
spring.datasource.url=jdbc:mysql://localhost:3306/tourmate
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

2. Build and run backend:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend API runs on http://localhost:8080/api.


## Frontend Setup

1. Configure the proxy for Angular to route API requests to Spring Boot backend. Example proxy.conf.json:

```bash
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

3. Run frontend:

```bash
cd tourmate_frontend
npm install
ng serve --proxy-config proxy.conf.json
```


3. Access the frontend at http://localhost:4200.

## API Endpoints
| Endpoint        | Method | Description                     |
|-----------------|--------|---------------------------------|
| /auth/register  | POST   | Register a new user             |
| /auth/login     | POST   | Log in and create session       |
| /auth/logout    | POST   | Log out and invalidate session  |
| /auth/me        | GET    | Get current logged-in user      |
| /preferences    | GET    | Get current user preferences    |
| /preferences    | POST   | Create new preferences          |
| /preferences    | PUT    | Update existing preferences     |



## Project Structure


## Security
- Authentication: Spring Security with session-based authentication.
- Password hashing: BCryptPasswordEncoder.
- Authorization: /preferences/** endpoints require authentication.
- CORS: Configured to allow frontend access during development.

## Notes
- Ensure the backend is running before the frontend.
- System notifications are displayed for errors like invalid login or failed preference saving.
- Session is stored using JSESSIONID cookie; frontend maintains user state via AuthService.

1. Enter your DB configuration in backend\src\main\resources\application.properties
2. To run backend:  mvn spring-boot:run
3. To run frontend: ng serve --proxy-config proxy.conf.json