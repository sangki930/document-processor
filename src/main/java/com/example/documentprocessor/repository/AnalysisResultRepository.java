package com.example.documentprocessor.repository;

import com.example.documentprocessor.model.AnalysisResult;
import com.example.documentprocessor.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    
    Optional<AnalysisResult> findByDocument(Document document);
    
    List<AnalysisResult> findByStatus(AnalysisResult.AnalysisStatus status);
    
    List<AnalysisResult> findBySentiment(String sentiment);
    
    List<AnalysisResult> findByCategory(String category);
}
