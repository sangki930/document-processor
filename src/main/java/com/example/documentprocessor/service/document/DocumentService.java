package com.example.documentprocessor.service.document;

import com.example.documentprocessor.model.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentService {

    Document uploadDocument(MultipartFile file);
    
    List<Document> getAllDocuments();
    
    Optional<Document> getDocumentById(Long id);
    
    List<Document> searchDocumentsByFileName(String fileName);
    
    List<Document> searchDocumentsByContent(String searchTerm);
    
    Resource downloadDocument(Long id);
    
    void deleteDocument(Long id);
    
    void processDocument(Long id);
}
