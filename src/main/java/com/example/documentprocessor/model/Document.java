package com.example.documentprocessor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private LocalDateTime uploadDateTime;

    @Column(nullable = false)
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private DocumentContent content;

    @OneToOne(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private AnalysisResult analysisResult;

    public Document() {
    }

    public Document(Long id, String fileName, String originalFileName, String contentType, Long fileSize,
                   LocalDateTime uploadDateTime, String storagePath, DocumentStatus status,
                   DocumentContent content, AnalysisResult analysisResult) {
        this.id = id;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadDateTime = uploadDateTime;
        this.storagePath = storagePath;
        this.status = status;
        this.content = content;
        this.analysisResult = analysisResult;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public DocumentContent getContent() {
        return content;
    }

    public void setContent(DocumentContent content) {
        this.content = content;
    }

    public AnalysisResult getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(AnalysisResult analysisResult) {
        this.analysisResult = analysisResult;
    }

    // Builder pattern
    public static DocumentBuilder builder() {
        return new DocumentBuilder();
    }

    public static class DocumentBuilder {
        private Long id;
        private String fileName;
        private String originalFileName;
        private String contentType;
        private Long fileSize;
        private LocalDateTime uploadDateTime;
        private String storagePath;
        private DocumentStatus status;
        private DocumentContent content;
        private AnalysisResult analysisResult;

        public DocumentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DocumentBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public DocumentBuilder originalFileName(String originalFileName) {
            this.originalFileName = originalFileName;
            return this;
        }

        public DocumentBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public DocumentBuilder fileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public DocumentBuilder uploadDateTime(LocalDateTime uploadDateTime) {
            this.uploadDateTime = uploadDateTime;
            return this;
        }

        public DocumentBuilder storagePath(String storagePath) {
            this.storagePath = storagePath;
            return this;
        }

        public DocumentBuilder status(DocumentStatus status) {
            this.status = status;
            return this;
        }

        public DocumentBuilder content(DocumentContent content) {
            this.content = content;
            return this;
        }

        public DocumentBuilder analysisResult(AnalysisResult analysisResult) {
            this.analysisResult = analysisResult;
            return this;
        }

        public Document build() {
            return new Document(id, fileName, originalFileName, contentType, fileSize, 
                               uploadDateTime, storagePath, status, content, analysisResult);
        }
    }

    public enum DocumentStatus {
        UPLOADED,
        PROCESSING,
        PROCESSED,
        FAILED
    }
}
