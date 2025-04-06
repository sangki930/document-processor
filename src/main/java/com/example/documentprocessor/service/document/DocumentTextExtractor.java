package com.example.documentprocessor.service.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class DocumentTextExtractor {

    private static final Logger logger = LoggerFactory.getLogger(DocumentTextExtractor.class);

    public String extractText(Resource resource, String contentType) {
        try (InputStream inputStream = resource.getInputStream()) {
            if (contentType == null) {
                return extractFromTxt(inputStream);
            }
            
            switch (contentType.toLowerCase()) {
                case "application/pdf":
                    return extractFromPdf(inputStream);
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                    return extractFromDocx(inputStream);
                case "application/msword":
                    return extractFromDoc(inputStream);
                case "text/plain":
                default:
                    return extractFromTxt(inputStream);
            }
        } catch (Exception e) {
            logger.error("Error extracting text from document", e);
            throw new DocumentProcessingException("Failed to extract text from document", e);
        }
    }
    
    private String extractFromPdf(InputStream inputStream) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    private String extractFromDocx(InputStream inputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }
    
    private String extractFromDoc(InputStream inputStream) throws Exception {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        }
    }
    
    private String extractFromTxt(InputStream inputStream) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
