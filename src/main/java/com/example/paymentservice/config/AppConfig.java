package com.example.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
public class AppConfig {

    // Example: simple ID generator bean (useful in payments)
    @Bean
    public Supplier<String> paymentIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }
}