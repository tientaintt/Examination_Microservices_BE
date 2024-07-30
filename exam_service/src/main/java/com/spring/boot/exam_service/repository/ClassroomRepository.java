package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.entity.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom,Long> {

    Optional<Classroom> findByClassCode(String code);
    @Query(value = "SELECT COUNT(*) FROM class_room WHERE manager_id = :managerId", nativeQuery = true)
     int countByUserProfile(String managerId);
    @Query(value = "select * FROM class_room where (class_name like  :searchText or class_code like :searchText)  and is_enable = :isEnable", nativeQuery = true)
    Page<Classroom> findAllSearchedClassRoomsByStatus(String searchText, Boolean isEnable, Pageable pageable);

    @Query(value = "select * FROM class_room where class_name like :className ", nativeQuery = true)
    Page<Classroom> findAllClassRoomsByClassName(String className, Pageable pageable);

    @Query(value = "select * FROM class_room where class_code like :classCode and is_enable = :isEnable", nativeQuery = true)
    Page<Classroom> findAllClassRoomsByClassCodeAndStatus(String classCode, Boolean isEnable, Pageable pageable);

    @Query(value = "select * FROM class_room where class_code like :classCode", nativeQuery = true)
    Page<Classroom> findAllClassRoomsByClassCode(String classCode, Pageable pageable);

    @Query(value = "select * FROM class_room where id = :classroomId and is_enable = :isEnable", nativeQuery = true)
    Optional<Classroom> findClassRoomByIdAndStatus(Long classroomId, Boolean isEnable);
    @Query(value = "Select cr.userID from Classroom cl left join ClassroomRegistration cr  on cl.id = cr.classRoom.id" +
            "where cl.id = :classroomId"

            ,nativeQuery = true)
    List<String> getAllUserIdOfClassroomByClassroomId(Long classroomId);
    @Query("SELECT cl FROM Classroom cl left join ClassroomRegistration cr on cl.id = cr.classRoom.id \n" +
            "where cr.userID like :userID \n" +
            "and (cl.classCode like :searchText or cl.className like :searchText)")
    Page<Classroom> findAllRegistedClassroomOfUser(String userID,String searchText, Pageable pageable);
    @Query("select cl from Classroom cl where cl.id = :classroomId and cl.isEnable=true")
    Optional<Classroom> findActiveClassroomById(Long classroomId);
}