package com.spring.boot.exam_service.repository;


import com.spring.boot.exam_service.entity.ClassroomRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRegistrationRepository extends JpaRepository<ClassroomRegistration,Long> {

    @Query(value = "SELECT email_address FROM user_profile u inner join classroom_registration cr \n" +
            "on u.user_id = cr.user_profile_user_id \n" +
            "where cr.class_room_id = :classroomId and cr.is_enable = true and u.is_enable = true;"
            , nativeQuery = true)
    List<String> findUserEmailOfClassroom(Long classroomId);

    Optional<ClassroomRegistration> findByClassRoomIdAndUserID(Long classroomId, String studentID);

    Long countAllByClassRoomId(Long classroomId);
}
