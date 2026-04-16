# StudentNotesApp

A simple Spring Boot RESTful web application with JWT security for managing student notes.

## Features

- User registration and login with JWT authentication
- BCrypt password encryption
- Create, read, update, and delete personal notes
- Notes belong to authenticated users only
- Minimalist web UI using HTML/CSS/JavaScript
- SQLite database with auto table creation

## Run the application

1. Open a terminal at the project root.
2. Run:
   ```bash
   mvn spring-boot:run
   ```
3. Open a browser and visit:
   - `http://localhost:8080/register.html`
   - `http://localhost:8080/login.html`
   - `http://localhost:8080/dashboard.html`

## Default storage

The app uses H2 in-memory database. No external database setup required - it starts automatically with the application.
