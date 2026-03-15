package com.example.rebookauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class RebookAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebookAuthServiceApplication.class, args);
    }

}
