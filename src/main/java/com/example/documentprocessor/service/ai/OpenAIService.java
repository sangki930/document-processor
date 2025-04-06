package com.example.documentprocessor.service.ai;

import com.example.documentprocessor.model.AnalysisResult;
import com.example.documentprocessor.model.Document;
import com.example.documentprocessor.model.DocumentContent;
import com.example.documentprocessor.repository.AnalysisResultRepository;
import com.example.documentprocessor.repository.DocumentContentRepository;
import com.example.documentprocessor.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OpenAIService implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);
    
    private final ChatClient chatClient;
    private final DocumentRepository documentRepository;
    private final DocumentContentRepository documentContentRepository;
    private final AnalysisResultRepository analysisResultRepository;
    
    public OpenAIService(ChatClient chatClient,
                        DocumentRepository documentRepository,
                        DocumentContentRepository documentContentRepository,
                        AnalysisResultRepository analysisResultRepository) {
        this.chatClient = chatClient;
        this.documentRepository = documentRepository;
        this.documentContentRepository = documentContentRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    @Override
    @Async
    @Transactional
    public void analyzeDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));
        
        DocumentContent content = documentContentRepository.findByDocument(document)
                .orElseThrow(() -> new IllegalArgumentException("Document content not found for document id: " + documentId));
        
        AnalysisResult analysisResult = analysisResultRepository.findByDocument(document)
                .orElseThrow(() -> new IllegalArgumentException("Analysis result not found for document id: " + documentId));
        
        try {
            // Update status to in progress
            analysisResult.setStatus(AnalysisResult.AnalysisStatus.IN_PROGRESS);
            analysisResultRepository.save(analysisResult);
            
            String text = content.getContent();
            
            // Generate summary
            String summary = generateSummary(text);
            analysisResult.setSummary(summary);
            
            // Extract keywords
            String keywords = extractKeywords(text);
            analysisResult.setKeywords(keywords);
            
            // Analyze sentiment
            String sentiment = analyzeSentiment(text);
            analysisResult.setSentiment(sentiment);
            
            // Categorize text
            String category = categorizeText(text);
            analysisResult.setCategory(category);
            
            // Update status to completed
            analysisResult.setStatus(AnalysisResult.AnalysisStatus.COMPLETED);
            analysisResult.setAnalysisDateTime(LocalDateTime.now());
            analysisResultRepository.save(analysisResult);
            
        } catch (Exception e) {
            logger.error("Error analyzing document: {}", documentId, e);
            
            // Update status to failed
            analysisResult.setStatus(AnalysisResult.AnalysisStatus.FAILED);
            analysisResult.setErrorMessage(e.getMessage());
            analysisResultRepository.save(analysisResult);
        }
    }

    @Override
    public String generateSummary(String text) {
        String promptText = "Please provide a concise summary of the following text. " +
                "Focus on the main points and key information. " +
                "The summary should be about 20% of the original length.\n\nText: {text}";
        
        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of("text", truncateIfNeeded(text)));
        
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    @Override
    public String extractKeywords(String text) {
        String promptText = "Extract the most important keywords or key phrases from the following text. " +
                "Return them as a comma-separated list.\n\nText: {text}";
        
        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of("text", truncateIfNeeded(text)));
        
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    @Override
    public String analyzeSentiment(String text) {
        String promptText = "Analyze the sentiment of the following text. " +
                "Classify it as one of: Very Positive, Positive, Neutral, Negative, Very Negative. " +
                "Return only the sentiment classification.\n\nText: {text}";
        
        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of("text", truncateIfNeeded(text)));
        
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    @Override
    public String categorizeText(String text) {
        String promptText = "Categorize the following text into one of these categories: " +
                "Business, Technology, Science, Health, Politics, Entertainment, Sports, Education, Other. " +
                "Return only the category name.\n\nText: {text}";
        
        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of("text", truncateIfNeeded(text)));
        
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }
    
    private String truncateIfNeeded(String text) {
        // OpenAI has token limits, so we need to truncate long texts
        // This is a simple character-based truncation
        // In a production system, you might want to use a more sophisticated approach
        final int MAX_LENGTH = 4000;
        if (text.length() <= MAX_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_LENGTH) + "... [truncated]";
    }
}
