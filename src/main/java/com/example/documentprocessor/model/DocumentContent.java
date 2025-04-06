package com.example.documentprocessor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "document_contents")
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Lob
    @Column(columnDefinition = "CLOB", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer wordCount;

    @Column(nullable = false)
    private Integer characterCount;

    public DocumentContent() {
    }

    public DocumentContent(Long id, Document document, String content, Integer wordCount, Integer characterCount) {
        this.id = id;
        this.document = document;
        this.content = content;
        this.wordCount = wordCount;
        this.characterCount = characterCount;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Integer getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(Integer characterCount) {
        this.characterCount = characterCount;
    }

    // Builder pattern
    public static DocumentContentBuilder builder() {
        return new DocumentContentBuilder();
    }

    public static class DocumentContentBuilder {
        private Long id;
        private Document document;
        private String content;
        private Integer wordCount;
        private Integer characterCount;

        public DocumentContentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DocumentContentBuilder document(Document document) {
            this.document = document;
            return this;
        }

        public DocumentContentBuilder content(String content) {
            this.content = content;
            return this;
        }

        public DocumentContentBuilder wordCount(Integer wordCount) {
            this.wordCount = wordCount;
            return this;
        }

        public DocumentContentBuilder characterCount(Integer characterCount) {
            this.characterCount = characterCount;
            return this;
        }

        public DocumentContent build() {
            return new DocumentContent(id, document, content, wordCount, characterCount);
        }
    }
}
