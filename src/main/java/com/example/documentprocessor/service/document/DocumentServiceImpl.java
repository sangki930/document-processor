package com.example.documentprocessor.service.document;

import com.example.documentprocessor.model.AnalysisResult;
import com.example.documentprocessor.model.Document;
import com.example.documentprocessor.model.DocumentContent;
import com.example.documentprocessor.repository.AnalysisResultRepository;
import com.example.documentprocessor.repository.DocumentContentRepository;
import com.example.documentprocessor.repository.DocumentRepository;
import com.example.documentprocessor.service.ai.AIService;
import com.example.documentprocessor.service.storage.StorageFileNotFoundException;
import com.example.documentprocessor.service.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    
    private final StorageService storageService;
    private final DocumentRepository documentRepository;
    private final DocumentContentRepository documentContentRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final DocumentTextExtractor textExtractor;
    private final AIService aiService;
    
    public DocumentServiceImpl(StorageService storageService, 
                              DocumentRepository documentRepository,
                              DocumentContentRepository documentContentRepository,
                              AnalysisResultRepository analysisResultRepository,
                              DocumentTextExtractor textExtractor,
                              AIService aiService) {
        this.storageService = storageService;
        this.documentRepository = documentRepository;
        this.documentContentRepository = documentContentRepository;
        this.analysisResultRepository = analysisResultRepository;
        this.textExtractor = textExtractor;
        this.aiService = aiService;
    }

    @Override
    @Transactional
    public Document uploadDocument(MultipartFile file) {
        String storedFileName = storageService.store(file);
        
        Document document = Document.builder()
                .fileName(storedFileName)
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadDateTime(LocalDateTime.now())
                .storagePath(storageService.load(storedFileName).toString())
                .status(Document.DocumentStatus.UPLOADED)
                .build();
        
        return documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        return documentRepository.findAllByOrderByUploadDateTimeDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> searchDocumentsByFileName(String fileName) {
        return documentRepository.findByFileNameContainingIgnoreCaseOrderByUploadDateTimeDesc(fileName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Document> searchDocumentsByContent(String searchTerm) {
        List<DocumentContent> contents = documentContentRepository.searchByContent(searchTerm);
        return contents.stream()
                .map(DocumentContent::getDocument)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id));
        
        try {
            return storageService.loadAsResource(document.getFileName());
        } catch (StorageFileNotFoundException e) {
            logger.error("File not found: {}", document.getFileName(), e);
            throw new IllegalStateException("File not found: " + document.getFileName(), e);
        }
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id));
        
        // Delete the physical file
        storageService.delete(document.getFileName());
        
        // Delete from database
        documentRepository.delete(document);
    }

    @Override
    @Async
    @Transactional
    public void processDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id));
        
        try {
            // Update status to processing
            document.setStatus(Document.DocumentStatus.PROCESSING);
            documentRepository.save(document);
            
            // Extract text from document
            Resource resource = storageService.loadAsResource(document.getFileName());
            String extractedText = textExtractor.extractText(resource, document.getContentType());
            
            // Count words and characters
            int wordCount = countWords(extractedText);
            int characterCount = extractedText.length();
            
            // Save document content
            DocumentContent content = DocumentContent.builder()
                    .document(document)
                    .content(extractedText)
                    .wordCount(wordCount)
                    .characterCount(characterCount)
                    .build();
            documentContentRepository.save(content);
            
            // Create analysis result with pending status
            AnalysisResult analysisResult = AnalysisResult.builder()
                    .document(document)
                    .analysisDateTime(LocalDateTime.now())
                    .status(AnalysisResult.AnalysisStatus.PENDING)
                    .build();
            analysisResultRepository.save(analysisResult);
            
            // Update document status to processed
            document.setStatus(Document.DocumentStatus.PROCESSED);
            document.setContent(content);
            document.setAnalysisResult(analysisResult);
            documentRepository.save(document);
            
            // Trigger AI analysis
            aiService.analyzeDocument(id);
            
        } catch (Exception e) {
            logger.error("Error processing document: {}", id, e);
            
            // Update status to failed
            document.setStatus(Document.DocumentStatus.FAILED);
            documentRepository.save(document);
            
            throw new DocumentProcessingException("Failed to process document: " + id, e);
        }
    }
    
    private int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        String[] words = text.split("\\s+");
        return words.length;
    }
}
