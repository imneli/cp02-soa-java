package com.fiap.cp02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Cp02Application {
    public static void main(String[] args) {
        SpringApplication.run(Cp02Application.class, args);
    }
}
