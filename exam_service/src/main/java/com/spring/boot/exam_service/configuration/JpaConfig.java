package com.spring.boot.exam_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {
    @Bean
    public AuditorAware<String> auditorProvider(AuditorAwareImpl auditorAwareImpl) {
        return auditorAwareImpl; // Trả về đối tượng đã được quản lý bởi Spring
    }
}
