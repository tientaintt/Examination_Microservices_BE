package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.dto.response.MyScoreResponse;
import com.spring.boot.exam_service.dto.response.StudentScoreResponse;
import com.spring.boot.exam_service.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    Optional<Score> findByMultipleChoiceTestIdAndUserID(Long testId, String userId);

    @Query("select new com.spring.boot.exam_service.dto.response.StudentScoreResponse( s.id,s.multipleChoiceTest.id, s.totalScore, s.createdDate, s.isLate, s.userID, null, null, s.targetScore) " +
            "FROM Score s " +
            "where s.multipleChoiceTest.id = :testId and (:search IS NULL OR :search = '' OR s.multipleChoiceTest.testName like :search)")
    Page<StudentScoreResponse> findAllScoreOfMultipleChoiceTest(Long testId, String search,Pageable pageable);
    @Query("select new com.spring.boot.exam_service.dto.response.StudentScoreResponse( s.id,s.multipleChoiceTest.id, s.totalScore, s.createdDate, s.isLate, s.userID, null, null, s.targetScore) " +
            "FROM Score s " +
            "where s.multipleChoiceTest.id = :testId ")
    List<StudentScoreResponse> findAllByMultipleChoiceTestId(Long testId);
    @Query("select new com.spring.boot.exam_service.dto.response.MyScoreResponse(s.id, s.totalScore, s.isLate, s.submittedDate, mc.id, mc.testName, c.id, c.subjectName, c.subjectCode, s.targetScore) " +
            "FROM Score s inner join MultipleChoiceTest mc on s.multipleChoiceTest.id = mc.id " +
            "inner join Subject c on mc.subject.id = c.id " +
            "where (:searchText IS NULL OR :searchText = '' OR  mc.testName like :searchText) and s.userID = :studentId and s.submittedDate > :dateFrom and s.submittedDate < :dateTo")
    Page<MyScoreResponse> findAllMyScores(String studentId, String searchText, Long dateFrom, Long dateTo , Pageable pageable);

    @Query("select new com.spring.boot.exam_service.dto.response.MyScoreResponse(s.id, s.totalScore, s.isLate, s.submittedDate, mc.id, mc.testName, c.id, c.subjectName, c.subjectCode, s.targetScore) " +
            "FROM Score s inner join MultipleChoiceTest mc on s.multipleChoiceTest.id = mc.id " +
            "inner join Subject c on mc.subject.id = c.id " +
            "where s.id=:scoreId ")
    MyScoreResponse findMyScoreResponseById(Long scoreId);

}
