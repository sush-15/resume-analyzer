#  ResumeAI - Resume Analyzer

An AI-powered resume analysis platform built with **Spring Boot** (backend) and **React** (frontend).

Upload your resume and get:
- **ATS Score** - for keyword match, section completeness, skills, formatting (0–100)
- **AI Feedback** - for suggestions, missing skills, and improvements via Gemini AI
- **Job Matching** - match your resume against available jobs with skill gap analysis
- **Dashboard** - track your progress over time to time

---

## Tech Stack

 Backend  - Java 17, Spring Boot 3.2, Spring Security (JWT) 
 Database - MySQL 8 (prod) / H2 in-memory (dev)             
 AI       - Google Gemini API                               
 Frontend - React 18, Recharts, Axios, React Router v6      

---

## Quick Start

### Prerequisites
- Java 17+ (or LTS version)
- Node.js 18+
- Maven 3.8+
- MySQL 8 (or use H2 for dev - don't install H2 separately. It runs inside your app as a Java library, just add the dependency to your pom.xml is enough and Maven will managed them)

---

### 1. Clone & Configure Backend

```bash
cd resume-analyzer/backend
```

**Option A - MySQL (production)**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/resume_analyzer_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

gemini.api.key=YOUR_GEMINI_API_KEY
```

Create the database:
```sql
CREATE DATABASE resume_analyzer_db;
```

**Option B - H2 in-memory (dev, no MySQL needed)**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Data is lost on restart. Good for testing.

---

### 2. Get a Gemini API Key (free)

1. Go to [https://aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)
2. Create a free API key
3. Paste it into `application.properties` as `gemini.api.key`

Without a key, the app falls back to rule based feedback (still works but just less detailed).

---

### 3. Run the Backend

```bash
cd resume-analyzer/backend
mvn spring-boot:run
```

Backend starts at **http://localhost:8080**

On first run, 8 sample jobs are seeded automatically.

---

### 4. Run the Frontend

```bash
cd resume-analyzer/frontend
npm install
npm start
```

Frontend starts at **http://localhost:3000**

---

## Features

### Authentication
- Register / Login with JWT tokens
- Auto-logout on token expiry (24h)
- BCrypt password hashing

### Resume Upload
- Drag & drop or click to upload PDF, DOC, DOCX (max 10MB)
- Instant ATS scoring on upload
- AI feedback generation (Gemini API with fallback)
- Automatic job matching

### ATS Score Breakdown

 **Component**         **Max Points** 
 Keyword Match        ->  40        
 Section Completeness -> 25      
 Technical Skills     -> 20        
 Formatting Quality   -> 15        
 **Total**            -> **100**   

### Job Matching
- Match any resume against all active jobs
- See matched and missing skills per job
- Filter by match quality (Excellent / Good / Partial)

### Dashboard
- Stats: total resumes, average ATS score, best score, job matches
- ATS score history chart
- Top matched and missing skills across all resumes
- Resume list with delete and view analysis actions

### Profile
- Account info overview
- Quick action buttons

---

## API Endpoints

### Auth
| Method | Endpoint              | Description       |
|--------|-----------------------|-------------------|
| POST   | `/api/auth/register`  | Register new user |
| POST   | `/api/auth/login`     | Login, get JWT    |

### Resume
 Method |     Endpoint                    |     Description              

 POST   - `/api/resume/upload`            -> Upload & analyze resume  
 GET    - `/api/resume/my-resumes`        -> List user's resumes      
 GET    - `/api/resume/{id}/ats-score`    -> Get ATS score            
 GET    - `/api/resume/{id}/ai-feedback`  -> Get AI feedback          
 GET    - `/api/resume/{id}/job-matches`  -> Get job matches          
 POST   - `/api/resume/{id}/reanalyze`    -> Re-run full analysis     
 DELETE - `/api/resume/{id}`              -> Soft-delete resume       

### Jobs
 Method  |   Endpoint              | Auth     | Description              

 GET    | `/api/jobs/public/all` - None     -> List all active jobs     
 GET    | `/api/jobs/match/{id}` - JWT      -> Match resume to jobs     
 POST   | `/api/jobs`            - ADMIN    -> Create a new job         
 PUT    | `/api/jobs/{id}`       - ADMIN    -> Update a job             
 DELETE | `/api/jobs/{id}`       - ADMIN    -> Deactivate a job         

### Dashboard
| Method | Endpoint         | Description          |
|--------|------------------|----------------------|
| GET    | `/api/dashboard` | Get dashboard stats  |

---

## Project Structure

```
resume-analyzer/
├── backend/
│   └── src/main/java/com/resumeanalyzer/
│       ├── config/          # Security, CORS, DataInitializer
│       ├── controller/      # REST controllers
│       ├── dto/             # Request/Response DTOs
│       ├── entity/          # JPA entities
│       ├── exception/       # Global exception handler
│       ├── repository/      # Spring Data JPA repos
│       ├── security/        # JWT filter, UserDetailsService
│       ├── service/         # Business logic
│       └── util/            # ATS calculator, PDF extractor
└── frontend/
    └── src/
        ├── components/
        │   ├── Auth/        # Login, Register
        │   ├── Dashboard/   # Dashboard
        │   ├── Jobs/        # Job matching
        │   ├── Layout/      # Navbar
        │   ├── Profile/     # Profile page
        │   └── Resume/      # Upload, Analysis
        └── services/
            └── api.js       # Axios client + all API calls
```

---

## Environment Variables

For production, use environment variables instead of hardcoding in `application.properties`:

```bash
export SPRING_DATASOURCE_PASSWORD=your_password
export GEMINI_API_KEY=your_key
export JWT_SECRET=your_256bit_secret
```

Then reference them in `application.properties`:
```properties
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
gemini.api.key=${GEMINI_API_KEY}
jwt.secret=${JWT_SECRET}
```

---

## Admin Account

To create an admin user, update the role directly in the database:
```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

Admin users can create, update, and deactivate jobs via the API.
---

## Limitations

- **Sample job data:** The 8 jobs shown in Job Matching are dummy data seeded for demonstration. They are not real openings.
- **No password reset:** Users can register and log in, but there is no "forgot password" flow yet. Planned: an email-based reset link with a time-limited token.
- **Static fallback suggestions:** Without a Gemini API key, the feedback suggestions are fixed text and do not change with the resume content.
             (Planned: generate them from the parsed resume text)
- **Literal keyword matching:** Similar terms such as "RESTful" and "REST API" are counted as different keywords.
             (Planned: synonym matching.)
  
