# Project Tasks Management Application

A full-stack web application for managing projects and tasks with user authentication. Built with Spring Boot (Java) backend and React (TypeScript) frontend.

## 🛠️ Tech Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 4.0.0** - Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database access
- **PostgreSQL 15** - Database
- **Flyway** - Database migrations
- **JWT (JSON Web Tokens)** - Token-based authentication
- **Maven** - Build tool

### Frontend
- **React 19.2.0** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **React Router** - Routing
- **Axios** - HTTP client
- **Tailwind CSS** - Styling
- **Radix UI** - UI components
- **Lucide React** - Icons

### Database
- **PostgreSQL 15** - Relational database

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Nginx** - Web server (production frontend)

## 📋 Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+**
- **Node.js 20+** and npm
- **PostgreSQL 15** (if running without Docker)
- **Docker** and **Docker Compose** (for containerized setup)

## 🚀 Quick Start with Docker (Recommended)

The easiest way to run the entire application is using Docker Compose:

### 1. Clone the repository
```bash
git clone <repository-url>
cd Project-Task
```

### 2. Start all services
```bash
docker compose up --build
```

This will start:
- PostgreSQL database on port `5432`
- Backend API on port `8080`
- Frontend on port `80` (http://localhost)

### 3. Access the application
- Frontend: http://localhost
- Backend API: http://localhost:8080
- Health Check: http://localhost:8080/health

### 4. Stop the services
```bash
docker compose down
```

To remove volumes (database data):
```bash
docker compose down -v
```

## 🔧 Manual Setup

### Database Setup

#### Option 1: Using Docker (PostgreSQL only)
```bash
docker run -d \
  --name project-tasks-db \
  -e POSTGRES_DB=project_tasks_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

#### Option 2: Local PostgreSQL Installation
1. Install PostgreSQL 15
2. Create a database:
```sql
CREATE DATABASE project_tasks_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE project_tasks_db TO postgres;
```

### Backend Setup

1. **Navigate to backend directory**
```bash
cd ProjectTasksApplication
```

2. **Set environment variables**
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=eW91ci1zZWNyZXQta2V5LWNoYW5nZS1pbi1wcm9kdWN0aW9uLW1pbi0yNTYtYml0cy12ZXJ5LWxvbmctc2VjcmV0LWtleS1mb3Itand0LXRva2VuLWdlbmVyYXRpb24=
```

Or create a `.env` file in the backend directory:
```env
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=eW91ci1zZWNyZXQta2V5LWNoYW5nZS1pbi1wcm9kdWN0aW9uLW1pbi0yNTYtYml0cy12ZXJ5LWxvbmctc2VjcmV0LWtleS1mb3Itand0LXRva2VuLWdlbmVyYXRpb24=
```

3. **Update application.yml** (if needed)
Edit `src/main/resources/application.yml` to match your database configuration:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/project_tasks_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

4. **Build and run**
```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

Or run the JAR file:
```bash
java -jar target/ProjectTasksApplication-0.0.1-SNAPSHOT.jar
```

The backend will start on **http://localhost:8080**

### Frontend Setup

1. **Navigate to frontend directory**
```bash
cd Frontend
```

2. **Install dependencies**
```bash
npm install
```

3. **Set environment variables**
Create a `.env` file in the frontend directory:
```env
VITE_API_URL=http://localhost:8080
```

4. **Run development server**
```bash
npm run dev
```

The frontend will start on **http://localhost:5173**

5. **Build for production**
```bash
npm run build
```

The production build will be in the `dist` directory.

## 📚 API Documentation

### Base URL
- Development: `http://localhost:8080`
- Production: Configured via environment variables

### Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

### API Endpoints

#### Authentication Endpoints

##### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "username": "username",
  "password": "password123"
}
```

**Response:**
```json
{
  "message": "User registered successfully"
}
```

##### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "username"
}
```

#### Project Endpoints

##### Get All Projects
```http
GET /api/projects
GET /api/projects?paginated=true&page=0&size=10
Authorization: Bearer <token>
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "My Project",
      "description": "Project description",
      "createdAt": "2025-12-16T10:00:00",
      "totalTasks": 5,
      "completedTasks": 2,
      "progressPercentage": 40.0
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

##### Get Project by ID
```http
GET /api/projects/{id}
Authorization: Bearer <token>
```

##### Create Project
```http
POST /api/projects
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "New Project",
  "description": "Project description"
}
```

##### Update Project
```http
PUT /api/projects/{id}
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Updated Project",
  "description": "Updated description"
}
```

##### Delete Project
```http
DELETE /api/projects/{id}
Authorization: Bearer <token>
```

#### Task Endpoints

##### Get Tasks by Project
```http
GET /api/projects/{projectId}/tasks
GET /api/projects/{projectId}/tasks?paginated=true&page=0&size=10&search=keyword&completed=false
Authorization: Bearer <token>
```

**Query Parameters:**
- `paginated` (boolean): Enable pagination (default: false)
- `page` (int): Page number (default: 0)
- `size` (int): Page size (default: 10)
- `search` (string): Search term for title/description
- `completed` (boolean): Filter by completion status

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Task Title",
      "description": "Task description",
      "completed": false,
      "dueDate": "2025-12-20T10:00:00",
      "createdAt": "2025-12-16T10:00:00",
      "projectId": 1
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

##### Create Task
```http
POST /api/projects/{projectId}/tasks
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "New Task",
  "description": "Task description",
  "dueDate": "2025-12-20T10:00:00"
}
```

##### Update Task
```http
PUT /api/tasks/{taskId}
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Updated Task",
  "description": "Updated description",
  "dueDate": "2025-12-21T10:00:00"
}
```

##### Update Task Status
```http
PATCH /api/tasks/{taskId}/status?completed=true
Authorization: Bearer <token>
```

##### Delete Task
```http
DELETE /api/tasks/{taskId}
Authorization: Bearer <token>
```

#### Health Check

##### Health Endpoint
```http
GET /health
```

**Response:**
```json
{
  "status": "UP"
}
```

## 🔐 Security

- **JWT Authentication**: All API endpoints (except `/api/auth/**` and `/health`) require authentication
- **Password Encryption**: Passwords are hashed using BCrypt
- **CORS**: Configured to allow requests from frontend origins
- **Input Validation**: All request bodies are validated using Jakarta Validation

## 🧪 Testing

### Backend Tests

Run unit tests:
```bash
cd ProjectTasksApplication
mvn test
```

### Frontend Tests

Run frontend tests (if configured):
```bash
cd Frontend
npm test
```

## 📁 Project Structure

```
Hahn-project/
├── ProjectTasksApplication/          # Backend (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/project/tasksapplication/
│   │   │   │       ├── config/      # Configuration classes
│   │   │   │       ├── controller/  # REST controllers
│   │   │   │       ├── dto/         # Data Transfer Objects
│   │   │   │       ├── exception/   # Exception handlers
│   │   │   │       ├── mapper/      # Entity mappers
│   │   │   │       ├── model/       # Entity models
│   │   │   │       ├── repository/  # Data repositories
│   │   │   │       ├── security/   # Security configuration
│   │   │   │       └── service/     # Business logic
│   │   │   └── resources/
│   │   │       ├── application.yml # Application configuration
│   │   │       └── db/migration/   # Flyway migrations
│   │   └── test/                   # Unit tests
│   └── pom.xml                      # Maven configuration
├── Frontend/                        # Frontend (React)
│   ├── src/
│   │   ├── api/                    # API clients
│   │   ├── components/             # React components
│   │   ├── context/                # React context
│   │   ├── hooks/                  # Custom hooks
│   │   ├── pages/                  # Page components
│   │   ├── routes/                 # Routing configuration
│   │   ├── types/                  # TypeScript types
│   │   └── utils/                  # Utility functions
│   ├── package.json
│   └── vite.config.ts
├── docker-compose.yml               # Docker Compose configuration
└── README.md                        # This file
```

## 🌐 Environment Variables

### Backend
- `DB_USERNAME`: PostgreSQL username (default: postgres)
- `DB_PASSWORD`: PostgreSQL password (default: postgres)
- `JWT_SECRET`: Secret key for JWT token generation (must be base64 encoded or at least 32 characters)
- `SPRING_DATASOURCE_URL`: Database connection URL (default: jdbc:postgresql://postgres:5432/project_tasks_db)

### Frontend
- `VITE_API_URL`: Backend API URL (default: http://localhost:8080)

## 🐛 Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```bash
# Find and kill the process
lsof -ti:8080 | xargs kill -9  # macOS/Linux
netstat -ano | findstr :8080   # Windows
```

**Database connection errors:**
- Verify PostgreSQL is running
- Check database credentials in `application.yml`
- Ensure database exists

**JWT token errors:**
- Verify `JWT_SECRET` is set correctly
- Ensure secret is base64 encoded or at least 32 characters long

### Frontend Issues

**CORS errors:**
- Verify backend CORS configuration allows your frontend origin
- Check `VITE_API_URL` environment variable

**Build errors:**
- Clear node_modules and reinstall: `rm -rf node_modules && npm install`
- Clear build cache: `rm -rf dist`

### Docker Issues

**Container won't start:**
```bash
# Check logs
docker compose logs backend
docker compose logs frontend
docker compose logs postgres

# Rebuild containers
docker compose up --build --force-recreate
```

**Database data persistence:**
- Data is stored in Docker volume `postgres_data`
- To reset: `docker compose down -v`

## 📝 License

This project is licensed under the MIT License.

## 👥 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For issues and questions, please open an issue in the repository.

---

**Happy Coding! 🚀**

