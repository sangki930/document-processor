package com.example.documentprocessor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String summary;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String keywords;

    @Column
    private String sentiment;

    @Column
    private String category;

    @Column(nullable = false)
    private LocalDateTime analysisDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String errorMessage;

    public AnalysisResult() {
    }

    public AnalysisResult(Long id, Document document, String summary, String keywords, String sentiment,
                         String category, LocalDateTime analysisDateTime, AnalysisStatus status, String errorMessage) {
        this.id = id;
        this.document = document;
        this.summary = summary;
        this.keywords = keywords;
        this.sentiment = sentiment;
        this.category = category;
        this.analysisDateTime = analysisDateTime;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getAnalysisDateTime() {
        return analysisDateTime;
    }

    public void setAnalysisDateTime(LocalDateTime analysisDateTime) {
        this.analysisDateTime = analysisDateTime;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Builder pattern
    public static AnalysisResultBuilder builder() {
        return new AnalysisResultBuilder();
    }

    public static class AnalysisResultBuilder {
        private Long id;
        private Document document;
        private String summary;
        private String keywords;
        private String sentiment;
        private String category;
        private LocalDateTime analysisDateTime;
        private AnalysisStatus status;
        private String errorMessage;

        public AnalysisResultBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AnalysisResultBuilder document(Document document) {
            this.document = document;
            return this;
        }

        public AnalysisResultBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public AnalysisResultBuilder keywords(String keywords) {
            this.keywords = keywords;
            return this;
        }

        public AnalysisResultBuilder sentiment(String sentiment) {
            this.sentiment = sentiment;
            return this;
        }

        public AnalysisResultBuilder category(String category) {
            this.category = category;
            return this;
        }

        public AnalysisResultBuilder analysisDateTime(LocalDateTime analysisDateTime) {
            this.analysisDateTime = analysisDateTime;
            return this;
        }

        public AnalysisResultBuilder status(AnalysisStatus status) {
            this.status = status;
            return this;
        }

        public AnalysisResultBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public AnalysisResult build() {
            return new AnalysisResult(id, document, summary, keywords, sentiment, 
                                     category, analysisDateTime, status, errorMessage);
        }
    }

    public enum AnalysisStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}
