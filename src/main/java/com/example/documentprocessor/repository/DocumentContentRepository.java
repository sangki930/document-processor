package com.example.documentprocessor.repository;

import com.example.documentprocessor.model.Document;
import com.example.documentprocessor.model.DocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentContentRepository extends JpaRepository<DocumentContent, Long> {
    
    Optional<DocumentContent> findByDocument(Document document);
    
    @Query("SELECT dc FROM DocumentContent dc WHERE dc.content LIKE CONCAT('%', :searchTerm, '%')")
    List<DocumentContent> searchByContent(@Param("searchTerm") String searchTerm);
}
