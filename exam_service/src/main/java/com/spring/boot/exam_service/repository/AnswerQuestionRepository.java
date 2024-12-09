package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.dto.response.AnswerResponse;
import com.spring.boot.exam_service.entity.Answer;
import com.spring.boot.exam_service.entity.AnswerQuestion;
import com.spring.boot.exam_service.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AnswerQuestionRepository extends JpaRepository<AnswerQuestion, Integer> {
    AnswerQuestion findByAnswerAndQuestion(Answer answer, Question question);
    @Modifying
    @Transactional
    void deleteByQuestion(Question question);
}
