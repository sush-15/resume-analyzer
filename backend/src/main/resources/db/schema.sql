-- ============================================
-- AI Resume Analyzer - Database Schema
-- Run this in MySQL Workbench or terminal
-- ============================================

CREATE DATABASE IF NOT EXISTS resume_analyzer_db;
USE resume_analyzer_db;

-- ============================================
-- Seed: Sample Jobs Data
-- (Tables are auto-created by Hibernate)
-- Run this AFTER starting the application once
-- ============================================

INSERT INTO jobs (title, company, description, required_skills, experience_level, job_type, location, is_active, created_at) VALUES
(
  'Java Backend Developer',
  'TechCorp Solutions',
  'Build and maintain scalable REST APIs using Spring Boot. Work with MySQL databases and implement microservices architecture.',
  '["Java","Spring Boot","MySQL","REST API","Maven","Git","Hibernate","JPA"]',
  'FRESHER', 'FULL_TIME', 'Bangalore, India', true, NOW()
),
(
  'Full Stack Developer',
  'StartupXYZ',
  'Develop full-stack web applications using React.js frontend and Spring Boot backend. Collaborate with cross-functional teams.',
  '["Java","Spring Boot","React","MySQL","JWT","REST API","Git","JavaScript","HTML","CSS"]',
  'JUNIOR', 'FULL_TIME', 'Hyderabad, India', true, NOW()
),
(
  'Software Engineer Intern',
  'BigTech Inc',
  'Backend development internship. Work on real-world projects with mentorship from senior engineers.',
  '["Java","Data Structures","Algorithms","SQL","OOP","Git","Problem Solving"]',
  'FRESHER', 'INTERNSHIP', 'Pune, India', true, NOW()
),
(
  'AI/ML Engineer',
  'AI Ventures Pvt Ltd',
  'Build AI-powered applications and integrate machine learning models into production systems.',
  '["Python","Machine Learning","REST API","Spring Boot","SQL","Git","Docker"]',
  'JUNIOR', 'FULL_TIME', 'Remote', true, NOW()
),
(
  'DevOps Engineer',
  'CloudSystems Ltd',
  'Manage CI/CD pipelines, containerization, and cloud infrastructure.',
  '["Docker","Kubernetes","AWS","Jenkins","Linux","Git","CI/CD","Shell Scripting"]',
  'JUNIOR', 'FULL_TIME', 'Mumbai, India', true, NOW()
),
(
  'Junior Java Developer',
  'Infosys',
  'Develop enterprise Java applications. Work with Spring framework and Oracle/MySQL databases.',
  '["Java","Spring","Spring Boot","SQL","Maven","Git","OOP","Design Patterns"]',
  'FRESHER', 'FULL_TIME', 'Chennai, India', true, NOW()
),
(
  'React Frontend Developer',
  'WebAgency Co',
  'Build responsive web applications using React.js. Integrate with REST APIs.',
  '["React","JavaScript","HTML","CSS","REST API","Git","Redux","TypeScript"]',
  'JUNIOR', 'FULL_TIME', 'Delhi, India', true, NOW()
),
(
  'Backend Developer Intern',
  'FinTech Startup',
  'Work on payment processing APIs and financial data management systems.',
  '["Java","Spring Boot","MySQL","REST API","Git","Maven","Security"]',
  'FRESHER', 'INTERNSHIP', 'Bangalore, India', true, NOW()
);
