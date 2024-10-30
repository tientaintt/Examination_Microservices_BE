package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.dto.response.AnswerResponse;
import com.spring.boot.exam_service.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT new com.spring.boot.exam_service.dto.response.AnswerResponse(answerQuestion.id,answerQuestion.isCorrect,answer.answer) " +
            "FROM " +
            "AnswerQuestion answerQuestion  LEFT JOIN " +
            "Answer answer on answerQuestion.answer.id= answer.id " +
            "WHERE answerQuestion.question.id = :idQuestion "
    )
    List<AnswerResponse> findListAnswerByIdQuestion(long idQuestion);

    @Query("SELECT answer.answer " +
            "FROM " +
            "AnswerQuestion answerQuestion  LEFT JOIN " +
            "Answer answer on answerQuestion.answer.id= answer.id " +
            "WHERE answerQuestion.question.id = :idQuestion and answerQuestion.isCorrect = true GROUP BY answerQuestion.question.id")
    String findCorrectAnswerByIdQuestion(long idQuestion);

    Optional<Answer> findByAnswer(String answer);
}
