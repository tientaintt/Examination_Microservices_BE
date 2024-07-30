package com.spring.boot.exam_service.repository;


import com.spring.boot.exam_service.entity.SubmittedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmittedQuestionRepository extends JpaRepository<SubmittedQuestion, Long> {

    List<SubmittedQuestion> findAllByScoreId(Long scoreId);
}
