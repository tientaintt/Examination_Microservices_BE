package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {

    Optional<Subject> findBySubjectCode(String code);
    @Query(value = "SELECT COUNT(*) FROM subject WHERE manager_id = :managerId", nativeQuery = true)
     int countByUserProfile(String managerId);
    @Query(value = "select * FROM subject where (subject_name like  :searchText or subject_code like :searchText)  and is_enable = :isEnable", nativeQuery = true)
    Page<Subject> findAllSearchedSubjectsByStatus(String searchText, Boolean isEnable, Pageable pageable);

    @Query(value = "select * FROM subject where subject_name like :subjectName ", nativeQuery = true)
    Page<Subject> findAllSubjectsBySubjectName(String subjectName, Pageable pageable);

    @Query(value = "select * FROM subject where subject_code like :subjectCode and is_enable = :isEnable", nativeQuery = true)
    Page<Subject> findAllSubjectsBySubjectCodeAndStatus(String subjectCode, Boolean isEnable, Pageable pageable);

    @Query(value = "select * FROM subject where subject_code like :subjectCode", nativeQuery = true)
    Page<Subject> findAllSubjectsBySubjectCode(String subjectCode, Pageable pageable);

    @Query(value = "select * FROM subject where id = :subjectId and is_enable = :isEnable", nativeQuery = true)
    Optional<Subject> findSubjectByIdAndStatus(Long subjectId, Boolean isEnable);
    @Query(value = "Select cr.userID from Subject cl right join SubjectRegistration cr  on cl.id = cr.subject.id " +
            "where cl.id = :subjectroomId"
    )
    List<String> getAllUserIdOfSubjectBySubjectId(Long subjectroomId);
    @Query("SELECT cl FROM Subject cl left join SubjectRegistration cr on cl.id = cr.subject.id \n" +
            "where cr.userID like :userID \n" +
            "and (cl.subjectCode like :searchText or cl.subjectName like :searchText)")
    Page<Subject> findAllRegisteredSubjectOfUser(String userID, String searchText, Pageable pageable);
    @Query("select cl from Subject cl where cl.id = :subjectroomId and cl.isEnable=true")
    Optional<Subject> findActiveSubjectById(Long subjectroomId);
}