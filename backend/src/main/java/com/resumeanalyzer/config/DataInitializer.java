package com.resumeanalyzer.config;

import com.resumeanalyzer.entity.Job;
import com.resumeanalyzer.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.logging.Logger;

/**
 * Seeds sample job data on startup if the jobs table is empty.
 * Works for both H2 and MySQL profiles.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = Logger.getLogger(DataInitializer.class.getName());

    @Bean
    public CommandLineRunner seedJobs(JobRepository jobRepository) {
        return args -> {
            if (jobRepository.count() > 0) {
                log.info("Jobs already seeded — skipping.");
                return;
            }

            log.info("Seeding sample job data...");

            jobRepository.save(createJob(
                "Java Backend Developer", "TechCorp Solutions",
                "Build and maintain scalable REST APIs using Spring Boot. Work with MySQL databases and implement microservices architecture.",
                "[\"Java\",\"Spring Boot\",\"MySQL\",\"REST API\",\"Maven\",\"Git\",\"Hibernate\",\"JPA\"]",
                Job.ExperienceLevel.FRESHER, Job.JobType.FULL_TIME, "Bangalore, India"
            ));

            jobRepository.save(createJob(
                "Full Stack Developer", "StartupXYZ",
                "Develop full-stack web applications using React.js frontend and Spring Boot backend.",
                "[\"Java\",\"Spring Boot\",\"React\",\"MySQL\",\"JWT\",\"REST API\",\"Git\",\"JavaScript\",\"HTML\",\"CSS\"]",
                Job.ExperienceLevel.JUNIOR, Job.JobType.FULL_TIME, "Hyderabad, India"
            ));

            jobRepository.save(createJob(
                "Software Engineer Intern", "BigTech Inc",
                "Backend development internship. Work on real-world projects with mentorship from senior engineers.",
                "[\"Java\",\"Data Structures\",\"Algorithms\",\"SQL\",\"OOP\",\"Git\"]",
                Job.ExperienceLevel.FRESHER, Job.JobType.INTERNSHIP, "Pune, India"
            ));

            jobRepository.save(createJob(
                "AI/ML Engineer", "AI Ventures Pvt Ltd",
                "Build AI-powered applications and integrate machine learning models into production systems.",
                "[\"Python\",\"Machine Learning\",\"REST API\",\"Spring Boot\",\"SQL\",\"Git\",\"Docker\"]",
                Job.ExperienceLevel.JUNIOR, Job.JobType.FULL_TIME, "Remote"
            ));

            jobRepository.save(createJob(
                "DevOps Engineer", "CloudSystems Ltd",
                "Manage CI/CD pipelines, containerization, and cloud infrastructure.",
                "[\"Docker\",\"Kubernetes\",\"AWS\",\"Jenkins\",\"Linux\",\"Git\",\"CI/CD\"]",
                Job.ExperienceLevel.JUNIOR, Job.JobType.FULL_TIME, "Mumbai, India"
            ));

            jobRepository.save(createJob(
                "Junior Java Developer", "Infosys",
                "Develop enterprise Java applications. Work with Spring framework and Oracle/MySQL databases.",
                "[\"Java\",\"Spring\",\"Spring Boot\",\"SQL\",\"Maven\",\"Git\",\"OOP\",\"Design Patterns\"]",
                Job.ExperienceLevel.FRESHER, Job.JobType.FULL_TIME, "Chennai, India"
            ));

            jobRepository.save(createJob(
                "React Frontend Developer", "WebAgency Co",
                "Build responsive web applications using React.js. Integrate with REST APIs.",
                "[\"React\",\"JavaScript\",\"HTML\",\"CSS\",\"REST API\",\"Git\",\"TypeScript\"]",
                Job.ExperienceLevel.JUNIOR, Job.JobType.FULL_TIME, "Delhi, India"
            ));

            jobRepository.save(createJob(
                "Backend Developer Intern", "FinTech Startup",
                "Work on payment processing APIs and financial data management systems.",
                "[\"Java\",\"Spring Boot\",\"MySQL\",\"REST API\",\"Git\",\"Maven\",\"Security\"]",
                Job.ExperienceLevel.FRESHER, Job.JobType.INTERNSHIP, "Bangalore, India"
            ));

            log.info("✅ Seeded 8 sample jobs successfully.");
        };
    }

    private Job createJob(String title, String company, String description,
                          String requiredSkills, Job.ExperienceLevel level,
                          Job.JobType type, String location) {
        Job job = new Job();
        job.setTitle(title);
        job.setCompany(company);
        job.setDescription(description);
        job.setRequiredSkills(requiredSkills);
        job.setExperienceLevel(level);
        job.setJobType(type);
        job.setLocation(location);
        job.setIsActive(true);
        return job;
    }
}
