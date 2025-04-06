package com.example.documentprocessor.controller;

import com.example.documentprocessor.model.AnalysisResult;
import com.example.documentprocessor.model.Document;
import com.example.documentprocessor.model.DocumentContent;
import com.example.documentprocessor.repository.AnalysisResultRepository;
import com.example.documentprocessor.repository.DocumentContentRepository;
import com.example.documentprocessor.service.document.DocumentService;
import com.example.documentprocessor.service.storage.StorageFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    private final DocumentService documentService;
    private final DocumentContentRepository documentContentRepository;
    private final AnalysisResultRepository analysisResultRepository;
    
    public DocumentController(DocumentService documentService,
                             DocumentContentRepository documentContentRepository,
                             AnalysisResultRepository analysisResultRepository) {
        this.documentService = documentService;
        this.documentContentRepository = documentContentRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    @GetMapping
    public String listDocuments(Model model) {
        List<Document> documents = documentService.getAllDocuments();
        model.addAttribute("documents", documents);
        return "documents/list";
    }

    @GetMapping("/{id}")
    public String viewDocument(@PathVariable Long id, Model model) {
        Document document = documentService.getDocumentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid document ID: " + id));
        
        Optional<DocumentContent> contentOpt = documentContentRepository.findByDocument(document);
        Optional<AnalysisResult> analysisResultOpt = analysisResultRepository.findByDocument(document);
        
        model.addAttribute("document", document);
        contentOpt.ifPresent(content -> model.addAttribute("content", content));
        analysisResultOpt.ifPresent(result -> model.addAttribute("analysisResult", result));
        
        return "documents/view";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "documents/upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        Document document = documentService.uploadDocument(file);
        
        // Trigger document processing asynchronously
        documentService.processDocument(document.getId());
        
        redirectAttributes.addFlashAttribute("message",
                "Successfully uploaded " + file.getOriginalFilename() + "!");
        
        return "redirect:/documents";
    }

    @GetMapping("/{id}/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid document ID: " + id));
        
        Resource file = documentService.downloadDocument(id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getOriginalFileName() + "\"")
                .body(file);
    }

    @GetMapping("/search")
    public String searchDocuments(@RequestParam(required = false) String fileName,
                                 @RequestParam(required = false) String content,
                                 Model model) {
        List<Document> documents;
        
        if (fileName != null && !fileName.isEmpty()) {
            documents = documentService.searchDocumentsByFileName(fileName);
            model.addAttribute("searchType", "fileName");
            model.addAttribute("searchTerm", fileName);
        } else if (content != null && !content.isEmpty()) {
            documents = documentService.searchDocumentsByContent(content);
            model.addAttribute("searchType", "content");
            model.addAttribute("searchTerm", content);
        } else {
            documents = documentService.getAllDocuments();
        }
        
        model.addAttribute("documents", documents);
        return "documents/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteDocument(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        documentService.deleteDocument(id);
        redirectAttributes.addFlashAttribute("message", "Document deleted successfully!");
        return "redirect:/documents";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
