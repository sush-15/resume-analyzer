package com.resumeanalyzer.service;

import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.entity.User;
import com.resumeanalyzer.repository.ResumeRepository;
import com.resumeanalyzer.repository.UserRepository;
import com.resumeanalyzer.util.PdfTextExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class ResumeService {

    private static final Logger log = Logger.getLogger(ResumeService.class.getName());

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final PdfTextExtractor textExtractor;

    @Value("${file.upload.dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_TYPES = Set.of(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
                         PdfTextExtractor textExtractor) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.textExtractor = textExtractor;
    }

    @Transactional
    public Resume uploadResume(MultipartFile file, String userEmail) throws IOException {
        validateFile(file);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String savedFileName = saveFileToDisk(file);
        String extractedText = textExtractor.extractText(file);
        log.info("Extracted " + extractedText.length() + " characters from resume");

        Resume resume = Resume.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .filePath(uploadDir + "/" + savedFileName)
                .fileType(file.getContentType())
                .extractedText(extractedText)
                .isActive(true)
                .build();

        Resume saved = resumeRepository.save(resume);
        log.info("Resume saved with ID: " + saved.getId());
        return saved;
    }

    public List<Resume> getResumesByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return resumeRepository.findByUserAndIsActiveTrueOrderByUploadedAtDesc(user);
    }

    public Resume getResumeById(Long resumeId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return resumeRepository.findByIdAndUser(resumeId, user)
                .orElseThrow(() -> new RuntimeException("Resume not found: " + resumeId));
    }

    @Transactional
    public void deleteResume(Long resumeId, String userEmail) {
        Resume resume = getResumeById(resumeId, userEmail);
        resume.setIsActive(false);
        resumeRepository.save(resume);
        log.info("Resume soft-deleted: " + resumeId);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new RuntimeException("Please select a file");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new RuntimeException("Only PDF, DOC, DOCX files are allowed");
        if (file.getSize() > 10 * 1024 * 1024)
            throw new RuntimeException("File size must not exceed 10MB");
    }

    private String saveFileToDisk(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        String uniqueName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), uploadPath.resolve(uniqueName),
                StandardCopyOption.REPLACE_EXISTING);
        return uniqueName;
    }
}
