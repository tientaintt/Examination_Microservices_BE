package com.spring.boot.identity_service.repository;


import com.spring.boot.identity_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepositoryRead extends JpaRepository<User, String> {
    @Query(value = "Select * from user u\n" +
            "\twhere u.is_enable = :isActive and u.id in (\n" +
            "    select user_id from user_roles ur\n" +
            "\t\twhere ur.user_id = :userId and is_email_address_verified=true and ur.roles_name = \"STUDENT\"\n" +
            "and ur.roles_name <> \"TEACHER\" " +
            "    )",
            nativeQuery = true)
    Optional<User> findVerifiedStudentByIdAndStatus(String userId, Boolean isActive);


    @Query(value = "SELECT * FROM user u " +
            "WHERE (display_name LIKE :searchText OR email_address LIKE :searchText) " +
            "  AND u.is_enable = :isActive " +
            "  AND u.id IN (" +
            "    SELECT ur.user_id " +
            "    FROM user_roles ur " +
            "    WHERE ur.roles_name LIKE 'STUDENT'" +
            "      AND NOT EXISTS (" +
            "        SELECT 1 FROM user_roles ur2 " +
            "        WHERE ur2.user_id = ur.user_id AND ur2.roles_name = 'TEACHER'" +
            "      )" +
            "  )",
            nativeQuery = true)
    Page<User> findAllSearchedStudentsByStatus(String searchText, Boolean isActive, Pageable pageable);

    //    @Query(value = "SELECT * FROM user u left join user_roles ur on u.id = ur.user_id " +
//            "where u.is_email_address_verified = true " +
//            "and u.is_enable = true " +
//            "and ur.roles_name like \"STUDENT\" \n" +
//            "and ur.roles_name <> \"TEACHER\" " +
//            "and (u.display_name like :searchText or u.email_address like :searchText)",
//            nativeQuery = true)
//    Page<User> findAllVerifiedStudents(String searchText, Pageable pageable);
//@Query(value = "SELECT u.id FROM user u left join user_roles ur on u.id = ur.user_id " +
//        "where u.is_email_address_verified = true " +
//                "and u.is_enable = true " +
//                "and ur.roles_name like \"STUDENT\" " +
//                "and ur.roles_name <> \"TEACHER\" ",nativeQuery = true)
//    List<String>findAllStudentId();
    @Query(value = "SELECT * FROM user u " +
            "WHERE u.is_email_address_verified = true " +
            "AND u.is_enable = true " +
            "AND EXISTS (" +
            "   SELECT 1 FROM user_roles ur " +
            "   WHERE ur.user_id = u.id " +
            "   AND ur.roles_name = 'STUDENT'" +
            ") " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM user_roles ur " +
            "   WHERE ur.user_id = u.id " +
            "   AND ur.roles_name = 'TEACHER'" +
            ") " +
            "AND (u.display_name LIKE :searchText OR u.email_address LIKE :searchText)",
            nativeQuery = true)
    Page<User> findAllVerifiedStudents(String searchText, Pageable pageable);

    @Query(value = "SELECT u.id FROM user u " +
            "WHERE u.is_email_address_verified = true " +
            "AND u.is_enable = true " +
            "AND EXISTS (" +
            "   SELECT 1 FROM user_roles ur " +
            "   WHERE ur.user_id = u.id " +
            "   AND ur.roles_name = 'STUDENT'" +
            ") " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM user_roles ur " +
            "   WHERE ur.user_id = u.id " +
            "   AND ur.roles_name = 'TEACHER'" +
            ")",
            nativeQuery = true)
    List<String> findAllStudentId();

}
