package com.shopai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * ShopAI India — Main Spring Boot Application
 * Enhanced E-Commerce Personalization Using AI Content Generation
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class ShopAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopAiApplication.class, args);
        System.out.println("================================================");
        System.out.println("  ShopAI India Backend Started Successfully!    ");
        System.out.println("  Server : http://localhost:8080                 ");
        System.out.println("  API    : http://localhost:8080/api             ");
        System.out.println("  Status : AI Personalization ACTIVE             ");
        System.out.println("================================================");
    }
}
