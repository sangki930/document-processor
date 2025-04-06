package com.example.documentprocessor.config;

import com.example.documentprocessor.service.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    CommandLineRunner initStorage(StorageService storageService) {
        return args -> {
            storageService.init();
        };
    }
}
