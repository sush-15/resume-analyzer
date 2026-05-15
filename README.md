# 🎯 AI-Powered Resume Analyzer, ATS Checker & Job Matcher

> A production-level full-stack web application that analyzes resumes using AI, calculates ATS compatibility scores, and matches candidates to job roles.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green?style=flat-square&logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?style=flat-square&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-purple?style=flat-square)
![Gemini AI](https://img.shields.io/badge/Gemini-AI-red?style=flat-square&logo=google)

---

## 📸 Features

| Feature | Description |
|---------|-------------|
| 🔐 **JWT Authentication** | Secure register/login with BCrypt password hashing |
| 📄 **Resume Upload** | Upload PDF, DOC, DOCX resumes (up to 10MB) |
| 📊 **ATS Score Calculator** | Scores resume 0–100 based on keywords, sections, skills, formatting |
| 🤖 **AI Feedback** | Google Gemini AI analyzes resume and gives actionable suggestions |
| 💼 **Job Matcher** | Matches your resume skills against real job listings |
| 📈 **Dashboard** | Visual analytics — score history, skill gaps, top job matches |

---

## 🏗️ Architecture

```
┌─────────────────┐         ┌──────────────────────┐         ┌──────────┐
│   React Frontend │ ──────▶ │  Spring Boot Backend  │ ──────▶ │  MySQL   │
│   (Port 3000)    │  REST   │     (Port 8080)        │   JPA   │   DB     │
│                  │  API    │                        │         │          │
│  - Login/Register│         │  - JWT Security        │         │ - users  │
│  - Resume Upload │         │  - PDF/DOCX Parsing    │         │ - resumes│
│  - ATS Dashboard │         │  - ATS Scoring Engine  │         │ - scores │
│  - Job Matcher   │         │  - Gemini AI API       │         │ - jobs   │
└─────────────────┘         └──────────────────────┘         └──────────┘
```

---

## 🛠️ Tech Stack

### Backend
- **Java 17** + **Spring Boot 3.2.5**
- **Spring Security 6** + **JWT (JJWT 0.12.5)**
- **Spring Data JPA** + **Hibernate**
- **MySQL 8.0**
- **Apache PDFBox 3.x** — PDF text extraction
- **Apache Tika 2.9** — DOC/DOCX text extraction
- **Google Gemini API** — AI resume feedback
- **Maven** — Build tool

### Frontend
- **React 18** + **React Router v6**
- **Axios** — HTTP client
- **Recharts** — Data visualization
- **React Toastify** — Notifications

---

## 📁 Project Structure

```
resume-analyzer/
├── backend/                          # Spring Boot application
│   ├── pom.xml
│   └── src/main/java/com/resumeanalyzer/
│       ├── config/                   # SecurityConfig, CorsConfig
│       ├── controller/               # Auth, Resume, Job, Dashboard APIs
│       ├── dto/                      # Request & Response DTOs
│       ├── entity/                   # JPA Entities (User, Resume, AtsScore...)
│       ├── exception/                # Global exception handler
│       ├── repository/               # Spring Data JPA repositories
│       ├── security/                 # JWT filter, token provider
│       ├── service/                  # Business logic layer
│       └── util/                     # PDF extractor, ATS calculator
│
├── frontend/                         # React application
│   ├── package.json
│   └── src/
│       ├── components/
│       │   ├── Auth/                 # Login, Register
│       │   ├── Dashboard/            # Analytics dashboard
│       │   ├── Jobs/                 # Job matcher
│       │   ├── Layout/               # Navbar
│       │   └── Resume/               # Upload & Analysis
│       └── services/api.js           # Axios API client
│
└── README.md
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0
- Node.js 18+

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/resume-analyzer.git
cd resume-analyzer
```

### 2. Configure MySQL
```sql
CREATE DATABASE resume_analyzer_db;
```

Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/resume_analyzer_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 3. Add Gemini API Key (Optional)
Get a free key at: https://aistudio.google.com/app/apikey

```properties
gemini.api.key=YOUR_GEMINI_API_KEY
```
> Without a key, the app uses a built-in fallback analyzer — it still works!

### 4. Start the Backend
```bash
cd backend
mvn spring-boot:run
```
Backend runs at: `http://localhost:8080`

### 5. Start the Frontend
```bash
cd frontend
npm install
npm start
```
Frontend runs at: `http://localhost:3000`

### 6. Seed Job Data
After the backend starts, run `backend/src/main/resources/db/schema.sql` in MySQL Workbench to insert 8 sample job listings.

---

## 📡 API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | ❌ | Register new user |
| POST | `/api/auth/login` | ❌ | Login, returns JWT |
| POST | `/api/resume/upload` | ✅ | Upload & analyze resume |
| GET | `/api/resume/my-resumes` | ✅ | Get all user resumes |
| GET | `/api/resume/{id}/ats-score` | ✅ | Get ATS score breakdown |
| GET | `/api/resume/{id}/ai-feedback` | ✅ | Get AI feedback |
| GET | `/api/resume/{id}/job-matches` | ✅ | Get job match results |
| DELETE | `/api/resume/{id}` | ✅ | Delete resume |
| GET | `/api/jobs/public/all` | ❌ | Get all job listings |
| GET | `/api/dashboard` | ✅ | Get dashboard analytics |

---

## 🧮 ATS Scoring Algorithm

The ATS score is calculated out of 100 points:

| Component | Points | Logic |
|-----------|--------|-------|
| **Keyword Match** | 40 pts | Matches resume against 50+ industry keywords |
| **Section Completeness** | 25 pts | Checks for Skills, Experience, Education, Projects, Contact |
| **Technical Skills** | 20 pts | Weighted scoring for high-value skills (Java=5, Spring Boot=5...) |
| **Formatting Quality** | 15 pts | Word count, bullet points, contact info, GitHub/LinkedIn |

**Score Grades:**
- A (85–100): Excellent — Very likely to pass ATS
- B (70–84): Good — Likely to pass most ATS systems
- C (55–69): Average — May pass some ATS systems
- D (40–54): Below Average — Needs improvement
- F (0–39): Poor — Major improvements required

---

## 🤖 AI Integration

Uses **Google Gemini Pro API** to analyze resume text and return:
- Strengths & Weaknesses
- Missing Skills
- Improvement Suggestions
- Better wording recommendations

Falls back to a rule-based analyzer if no API key is configured.

---

## 🗄️ Database Schema

```
users          → id, full_name, email, password, role, created_at
resumes        → id, user_id, file_name, file_path, extracted_text, uploaded_at
ats_scores     → id, resume_id, user_id, overall_score, keyword_score, ...
jobs           → id, title, company, required_skills (JSON), experience_level, ...
skill_matches  → id, user_id, resume_id, job_id, match_score, matched_skills, ...
ai_feedback    → id, resume_id, user_id, feedback_text, suggestions, ...
```

---

## 🎓 Interview Talking Points

**Q: How does ATS scoring work?**
> ATS systems parse resumes and match keywords against job descriptions. Our engine replicates this by scoring keyword density, section completeness, technical skill presence, and formatting quality.

**Q: How is JWT authentication implemented?**
> On login, the server generates a signed JWT token using HMAC-SHA256. The client stores it in localStorage and sends it in the `Authorization: Bearer <token>` header on every request. The `JwtAuthenticationFilter` validates it before each API call.

**Q: How does the job matching work?**
> We parse the required skills JSON from each job, then check how many of those skills appear in the resume text. Match score = (matched skills / total required skills) × 100.

**Q: Why no Lombok?**
> Java 24 has annotation processing issues with Lombok. All entities and DTOs use explicit getters, setters, and inner Builder classes — which is actually better for understanding the code.

---

## 🚀 Future Improvements

- [ ] Resume version comparison
- [ ] Email notifications for job matches
- [ ] Admin panel for managing jobs
- [ ] Docker + Docker Compose setup
- [ ] Deploy to AWS / Railway / Render
- [ ] Resume PDF generation
- [ ] LinkedIn profile import

---

## 👨‍💻 Author

Built as a portfolio project demonstrating:
- Production-level Spring Boot architecture
- JWT security implementation
- AI API integration
- Full-stack React + Java development
- Clean code practices without Lombok

---

## 📄 License

MIT License — free to use for learning and portfolio purposes.
