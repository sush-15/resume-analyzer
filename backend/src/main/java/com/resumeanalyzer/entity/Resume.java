package com.resumeanalyzer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_type", length = 20)
    private String fileType;

    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Resume() {}

    @PrePersist
    protected void onCreate() { uploadedAt = LocalDateTime.now(); }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getFileType() { return fileType; }
    public String getExtractedText() { return extractedText; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public Boolean getIsActive() { return isActive; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private String fileName;
        private String filePath;
        private String fileType;
        private String extractedText;
        private Boolean isActive = true;

        public Builder user(User user) { this.user = user; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder fileType(String fileType) { this.fileType = fileType; return this; }
        public Builder extractedText(String extractedText) { this.extractedText = extractedText; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive; return this; }

        public Resume build() {
            Resume r = new Resume();
            r.user = this.user;
            r.fileName = this.fileName;
            r.filePath = this.filePath;
            r.fileType = this.fileType;
            r.extractedText = this.extractedText;
            r.isActive = this.isActive;
            return r;
        }
    }
}
