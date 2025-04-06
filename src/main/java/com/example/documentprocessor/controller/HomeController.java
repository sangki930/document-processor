package com.example.documentprocessor.controller;

import com.example.documentprocessor.repository.DocumentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DocumentRepository documentRepository;
    
    public HomeController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        long documentCount = documentRepository.count();
        model.addAttribute("documentCount", documentCount);
        return "home";
    }
}
