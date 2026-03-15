package com.example.rebookauthservice.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
public class AppConfig {

}
