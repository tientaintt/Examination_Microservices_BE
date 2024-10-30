package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionTypeRepository extends JpaRepository<QuestionType, Long> {
    Optional<QuestionType> findByTypeQuestion(String questionType);
}
