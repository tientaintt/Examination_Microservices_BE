package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.dto.response.MyMultipleChoiceTestResponse;

import com.spring.boot.exam_service.dto.response.ReportTestByMonthResponse;
import com.spring.boot.exam_service.entity.MultipleChoiceTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceTestRepository extends JpaRepository<MultipleChoiceTest,Long> {
    int countByCreatedBy(String idUser);
    @Query(value = "select * FROM multiple_choice_test \n" +
            "\twhere subject_id = :subjectId \n" +
            "\t\tand end_date <= :unixTimeNow \n" +
            "\t\tand test_name like :searchText", nativeQuery = true)
    Page<MultipleChoiceTest> findEndedMultipleChoiceTestOfClassroomByClassroomId(Long subjectId, Long unixTimeNow, String searchText, Pageable pageable);

    @Query(value = "select * FROM multiple_choice_test \n" +
            "\twhere subject_id = :subjectId \n" +
            "\t\tand end_date > :unixTimeNow \n" +
            "\t\tand test_name like :searchText", nativeQuery = true)
    Page<MultipleChoiceTest> findNotEndedMultipleChoiceTestOfClassroomByClassroomId(Long subjectId,Long unixTimeNow, String searchText, Pageable pageable);

    @Query("select new com.spring.boot.exam_service.dto.response.MyMultipleChoiceTestResponse(mct.id, mct.createdBy , mct.startDate , mct.endDate, mct.testName,mct.description, mct.testingTime, \n" +
            "\tmct.subject.id , cr.subjectName, cr.subjectCode , cr.description, false)\n" +
            "\tFROM MultipleChoiceTest mct left join Subject cr on mct.subject.id = cr.id\n" +
            "\twhere mct.subject.id IN (\n" +
            "\t\tSELECT crr.subject.id FROM SubjectRegistration crr \n" +
            "\t\t\twhere crr.userID = :myId \n" +
            "\t\t\tand crr.isEnable = true\n" +
            "    ) and mct.endDate <= :unixTimeNow and mct.testName like :searchText")
    Page<MyMultipleChoiceTestResponse> findMyEndedMultipleChoiceTests(String myId, Long unixTimeNow, String searchText, Pageable pageable);

    @Query("select new com.spring.boot.exam_service.dto.response.MyMultipleChoiceTestResponse(mct.id, mct.createdBy , mct.startDate , mct.endDate, mct.testName,mct.description, mct.testingTime, \n" +
            "\tmct.subject.id , cr.subjectName , cr.subjectCode , cr.description, false)\n" +
            "\tFROM MultipleChoiceTest mct left join Subject cr on mct.score.id = cr.id\n" +
            "\twhere mct.subject.id IN (\n" +
            "\t\tSELECT crr.subject.id FROM SubjectRegistration crr \n" +
            "\t\t\twhere crr.userID = :myId \n" +
            "\t\t\tand crr.isEnable = true\n" +
            "    ) and mct.endDate > :unixTimeNow and mct.testName like :searchText")
    Page<MyMultipleChoiceTestResponse> findMyNotEndedMultipleChoiceTests(String myId, Long unixTimeNow, String searchText, Pageable pageable);

    @Query("select new com.spring.boot.exam_service.dto.response.MyMultipleChoiceTestResponse(mct.id, mct.createdBy , mct.startDate , mct.endDate, mct.testName,mct.description, mct.testingTime, \n" +
            "\tmct.subject.id , cr.subjectName , cr.subjectCode , cr.description, false)\n" +
            "\tFROM MultipleChoiceTest mct left join Subject cr on mct.subject.id = cr.id\n" +
            "\twhere mct.subject.id IN (\n" +
            "\t\tSELECT crr.subject.id FROM SubjectRegistration crr \n" +
            "\t\t\twhere crr.userID = :userId \n" +
            "\t\t\tand crr.isEnable = true\n" +
            "    ) and mct.startDate > :unixTime2WeeksAgo and mct.startDate <= :unixTime2WeeksLater and mct.testName like :searchText")
    List<MyMultipleChoiceTestResponse> find2WeeksAroundMCTest(String userId, Long unixTime2WeeksAgo , Long unixTime2WeeksLater,String searchText, Pageable pageable);
    @Query("select new com.spring.boot.exam_service.dto.response.MyMultipleChoiceTestResponse(mct.id, mct.createdBy , mct.startDate , mct.endDate, mct.testName,mct.description, mct.testingTime, \n" +
            "\tmct.subject.id , cr.subjectName , cr.subjectCode , cr.description, false)\n" +
            "\tFROM MultipleChoiceTest mct left join Subject cr on mct.subject.id = cr.id\n" +
            "\twhere mct.subject.id IN (\n" +
            "\t\tSELECT crr.subject.id FROM SubjectRegistration crr \n" +
            "\t\t\twhere crr.userID = :userId \n" +
            "\t\t\tand crr.isEnable = true\n" +
            "    ) and ( mct.startDate >= :startDay and mct.startDate <= :endDay ) and mct.testName like :searchText")
    Page<MyMultipleChoiceTestResponse> findMCTestByDay(String userId,Long startDay, Long endDay,String searchText, Pageable pageable);

    @Query("select new com.spring.boot.exam_service.dto.response.MyMultipleChoiceTestResponse(mct.id, mct.createdBy , mct.startDate , mct.endDate, mct.testName,mct.description, mct.testingTime, \n" +
            "\tmct.subject.id , cr.subjectName , cr.subjectCode , cr.description, false)\n" +
            "\tFROM MultipleChoiceTest mct left join Subject cr on mct.subject.id = cr.id\n" +
            "\twhere mct.id = :testId and mct.isEnable = true " +
            "\t and mct.subject.id IN (\n" +
            "\t\tSELECT crr.subject.id FROM SubjectRegistration crr \n" +
            "\t\t\twhere crr.userID = :studentId \n" +
            "\t\t\tand crr.isEnable = true)")
    MyMultipleChoiceTestResponse findMultipleChoiceTestInformation(Long testId, String studentId);

    @Query("SELECT new com.spring.boot.exam_service.dto.response.ReportTestByMonthResponse(MONTH(t.createdDate),COUNT(t)) " +
            "FROM MultipleChoiceTest t " +
            "WHERE t.createdBy= :userId " +
            "GROUP BY MONTH(t.createdDate)")
    List<ReportTestByMonthResponse> countTestsByMonthByCreateBy(String userId);
}
