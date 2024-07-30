package com.spring.boot.exam_service.repository;


import com.spring.boot.exam_service.entity.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion,Long> {
    @Query(value = "SELECT question_id FROM test_question where is_enable = true and multiple_choice_test_id = :testId", nativeQuery = true)
    List<Long> findQuestionIdsOfTest(Long testId);

    Long countAllByMultipleChoiceTestId(Long testId);
}
