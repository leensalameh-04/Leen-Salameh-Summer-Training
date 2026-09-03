# Task Management API

A production-grade RESTful API built with **Spring Boot 3 / 4**, **Spring Data JPA**, **H2 / PostgreSQL**, **Jakarta Validation**, and **Spring Boot Actuator**.

---

## 🌟 Architecture & Features

- **Layered Architecture**: Clear separation of concerns between `controller`, `service`, `repository`, `model`, `dto`, `mapper`, and `exception` layers.
- **Relational Data Mapping**: Bi-directional `@OneToMany` / `@ManyToOne` relationship between `Project` and `Task`.
- **Validation & Exception Handling**: Centralized error responses using `@RestControllerAdvice` with custom exceptions (`TaskNotFoundException`, `ProjectNotFoundException`, `InvalidTaskStatusException`, `DuplicateProjectNameException`).
- **Dynamic Filtering, Pagination & Sorting**: Spring Data `Specification` criteria query API supporting paged filtering by `status`, `priority`, `projectId`, and `overdue` flag.
- **Operational Monitoring**: Spring Boot Actuator health & info endpoints (`/actuator/health`, `/actuator/info`).
- **Multi-Profile Configuration**:
  - `dev`: In-memory H2 database with web console (`/h2-console`).
  - `prod`: External PostgreSQL / MySQL database driven by environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** JDK or higher
- **Maven 3.8+**

### Building the Executable JAR
To run tests and compile the standalone executable JAR:
```bash
mvn clean package
```

### Running the Application

#### 1. Development Profile (H2 In-Memory Database)
```bash
java -jar target/task-management-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```
- **Base URL**: `http://localhost:8081`
- **H2 Console**: `http://localhost:8081/h2-console`
  - **JDBC URL**: `jdbc:h2:mem:taskdb`
  - **Username**: `sa`
  - **Password**: `password`

#### 2. Production Profile (PostgreSQL Database)
```bash
export DB_URL=jdbc:postgresql://localhost:5432/taskdb
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

java -jar target/task-management-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## 📑 Environment Variables Reference

| Variable Name | Default Value | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/taskdb` | JDBC Database Connection URL |
| `DB_USERNAME` | `postgres` | Database Username |
| `DB_PASSWORD` | `postgres` | Database Password |
| `DB_DRIVER` | `org.postgresql.Driver` | JDBC Driver Class Name |

---

## 📡 API Endpoints Reference

### Projects API (`/api/projects`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/projects` | Create a new project | `201 CREATED` |
| `GET` | `/api/projects` | Fetch all projects | `200 OK` |
| `GET` | `/api/projects/{id}` | Fetch project by ID | `200 OK` / `404` |
| `PUT` | `/api/projects/{id}` | Update existing project | `200 OK` / `404` / `409` |
| `DELETE` | `/api/projects/{id}` | Delete project by ID | `204 NO CONTENT` / `404` |
| `GET` | `/api/projects/{projectId}/tasks` | Fetch tasks assigned to a project | `200 OK` |

### Tasks API (`/api/tasks`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/tasks` | Create a new task | `201 CREATED` |
| `GET` | `/api/tasks` | Get paged & filtered tasks (`status`, `priority`, `projectId`, `overdue`, `page`, `size`, `sortBy`, `sortDir`) | `200 OK` |
| `GET` | `/api/tasks/{id}` | Fetch task by ID | `200 OK` / `404` |
| `PUT` | `/api/tasks/{id}` | Update task details | `200 OK` / `404` |
| `PATCH` | `/api/tasks/{id}/status` | Update task status (`?status=IN_PROGRESS`) | `200 OK` / `400` / `404` |
| `GET` | `/api/tasks/overdue` | Fetch overdue tasks | `200 OK` |
| `DELETE` | `/api/tasks/{id}` | Delete task by ID | `204 NO CONTENT` / `404` |

### Monitoring API (`/actuator`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `GET` | `/actuator/health` | Application health details | `200 OK` |
| `GET` | `/actuator/info` | Application build & metadata info | `200 OK` |

---

## 📮 Postman Collection
Import `task_management_api.postman_collection.json` into Postman to test all API endpoints and actuator monitors.
