package com.spring.boot.exam_service.configuration;

import com.spring.boot.exam_service.entity.QuestionType;

import com.spring.boot.exam_service.repository.QuestionTypeRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.HashSet;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {



    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(QuestionTypeRepository questionTypeRepository) {
        log.info("Initializing application.....");
        return args -> {
            if(questionTypeRepository.findByTypeQuestion("Multiple Choice").isEmpty()){
                questionTypeRepository.save(QuestionType.builder().typeQuestion("Multiple Choice").build());
            }
            if(questionTypeRepository.findByTypeQuestion("Fill in the blank").isEmpty()){
                questionTypeRepository.save(QuestionType.builder().typeQuestion("Fill in the blank").build());
            }
            if(questionTypeRepository.findByTypeQuestion("True/False").isEmpty()){
                questionTypeRepository.save(QuestionType.builder().typeQuestion("True/False").build());
            }
        };
    }
}
