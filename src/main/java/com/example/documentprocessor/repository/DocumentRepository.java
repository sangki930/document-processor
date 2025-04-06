package com.example.documentprocessor.repository;

import com.example.documentprocessor.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findAllByOrderByUploadDateTimeDesc();
    
    List<Document> findByFileNameContainingIgnoreCaseOrderByUploadDateTimeDesc(String fileName);
    
    List<Document> findByStatus(Document.DocumentStatus status);
}
