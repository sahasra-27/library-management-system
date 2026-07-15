package com.smartlibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class SmartLibraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartLibraryApplication.class, args);
    }
}
