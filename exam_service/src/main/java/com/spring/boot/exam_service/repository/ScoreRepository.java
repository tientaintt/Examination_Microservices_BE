package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.dto.response.MyScoreResponse;
import com.spring.boot.exam_service.dto.response.StudentScoreResponse;
import com.spring.boot.exam_service.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    Optional<Score> findByMultipleChoiceTestIdAndUserID(Long testId, String userId);

    @Query("select new com.spring.boot.exam_service.dto.response.StudentScoreResponse( s.id,s.multipleChoiceTest.id, s.totalCore, s.createdDate, s.isLate, null, null, null, s.targetScore) " +
            "FROM Score s " +
            "where s.multipleChoiceTest.id = :testId ")
    Page<StudentScoreResponse> findAllScoreOfMultipleChoiceTest(Long testId, String search,Pageable pageable);

    @Query("select new com.spring.boot.exam_service.dto.response.MyScoreResponse(s.id, s.totalCore, s.isLate, s.submittedDate, mc.id, mc.testName, c.id, c.className, c.classCode, s.targetScore) " +
            "FROM Score s inner join MultipleChoiceTest mc on s.multipleChoiceTest.id = mc.id " +
            "inner join Classroom c on mc.classRoom.id = c.id " +
            "where mc.testName like :searchText and s.userId = :studentId and s.submittedDate > :dateFrom and s.submittedDate < :dateTo")
    Page<MyScoreResponse> findAllMyScores(Long studentId, String searchText, Long dateFrom, Long dateTo , Pageable pageable);
}
