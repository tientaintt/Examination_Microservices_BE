package com.spring.boot.exam_service.repository;


import com.spring.boot.exam_service.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "select * FROM question where id = :id and is_enable = :isEnable", nativeQuery = true)
    Optional<Question> findByIdAndStatus(Long id, Boolean isEnable);

    @Query(value = "SELECT * FROM question where id IN :ids", nativeQuery = true)
    List<Question> findAllByIds(List<Long> ids);

    @Query(value = "SELECT * FROM question where id IN :ids", nativeQuery = true)
    Page<Question> findAllByIds(List<Long> ids,Pageable pageable);

    @Query(value = "select * FROM question where " +
            "question_group_id = :questionGroupId " +
            "and is_enable = :isActiveQuestion " +
            "and (content LIKE :searchText OR :searchText IS NULL OR :searchText = '')", nativeQuery = true)
    Page<Question> getQuestionsOfQuestionGroupByQuestionGroupId(Long questionGroupId,String searchText, boolean isActiveQuestion, Pageable pageable);

    @Query(value = "SELECT * FROM question where \n" +
            "\tquestion_group_id = :questionGroupId \n" +
            "    and is_enable = true \n" +
            "    ORDER BY RAND() LIMIT :numberOfQuestion", nativeQuery = true)
    List<Question> findRandomQuestionByQuestionGroupId(Long questionGroupId, Long numberOfQuestion);

    @Query(value = "SELECT * FROM question q where \n" +
            "q.is_enable = :isActiveQuestion  \n" +
            "and (q.content like :searchText) \n" +
            "and  q.question_group_id \n" +
            "\tIN (SELECT id FROM question_group qg where qg.subject" +
            "_id = :subjectId)", nativeQuery = true)
    Page<Question> getQuestionsOfSubject(Long subjectId, String searchText, boolean isActiveQuestion, Pageable pageable);
    @Query("SELECT count (*)" +
            "FROM Question q " +
            "where q.questionGroup.id =:questionGroupId and q.isEnable = true")
    Long countQuestionsByQuestionGroupId(Long questionGroupId);
    @Query("SELECT q.content " +
            "FROM Question q " +
            "where q.id=:questionId ")
    String getContentQuestionByQuestionId(Long questionId);
}
