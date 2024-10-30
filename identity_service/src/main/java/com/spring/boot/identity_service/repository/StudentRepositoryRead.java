package com.spring.boot.identity_service.repository;


import com.spring.boot.identity_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepositoryRead extends JpaRepository<User, String> {
    @Query(value = "Select * from user u\n" +
            "\twhere u.is_enable = :isActive and u.id in (\n" +
            "    select user_id from user_roles ur\n" +
            "\t\twhere ur.user_id = :userId and is_email_address_verified=true and ur.roles_name = \"STUDENT\"\n" +
            "    )",
            nativeQuery = true)
    Optional<User> findVerifiedStudentByIdAndStatus(String userId, Boolean isActive);


    @Query(value = "Select * from user u\n" +
            "\twhere (display_name like :searchText or email_address like :searchText) and u.is_enable = :isActive \n" +
            "\t\tand u.id in (\n" +
            "\t\tselect user_id from user_roles ur\n" +
            "\t\t\twhere ur.roles_name like \"STUDENT\"\n" +
            "\t\t)",
            nativeQuery = true)
    Page<User> findAllSearchedStudentsByStatus(String searchText, Boolean isActive, Pageable pageable);

    @Query(value = "SELECT * FROM user u left join user_roles ur on u.id = ur.user_id " +
            "where u.is_email_address_verified = true " +
            "and u.is_enable = true " +
            "and ur.roles_name like \"STUDENT\" \n" +
            "and (u.display_name like :searchText or u.email_address like :searchText)",
            nativeQuery = true)
    Page<User> findAllVerifiedStudents(String searchText, Pageable pageable);


}
