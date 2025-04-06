package com.example.documentprocessor.service.ai;

public interface AIService {

    void analyzeDocument(Long documentId);
    
    String generateSummary(String text);
    
    String extractKeywords(String text);
    
    String analyzeSentiment(String text);
    
    String categorizeText(String text);
}
