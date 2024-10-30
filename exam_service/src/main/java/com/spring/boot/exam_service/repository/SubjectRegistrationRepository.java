package com.spring.boot.exam_service.repository;


import com.spring.boot.exam_service.entity.SubjectRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRegistrationRepository extends JpaRepository<SubjectRegistration,Long> {

    @Query(value = "SELECT email_address FROM user_profile u inner join subject_registration cr \n" +
            "on u.user_id = cr.user_profile_user_id \n" +
            "where cr.class_room_id = :subjectId and cr.is_enable = true and u.is_enable = true;"
            , nativeQuery = true)
    List<String> findUserEmailOfSubject(Long subjectId);

    Optional<SubjectRegistration> findBySubjectIdAndUserID(Long subjectId, String studentID);

    Long countAllBySubjectId(Long subjectId);
}
