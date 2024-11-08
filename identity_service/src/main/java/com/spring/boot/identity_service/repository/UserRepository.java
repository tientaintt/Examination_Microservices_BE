package com.spring.boot.identity_service.repository;

import com.spring.boot.identity_service.entity.Role;
import com.spring.boot.identity_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository< User,String> {
    boolean existsByUsername(String username);
    int countAllByRolesIs(Role role);
    Optional<User> findByUsername(String username);
    @Query(value = "SELECT * FROM user where username = :loginName",
            nativeQuery = true)
    Optional<User> findOneByUsername(String loginName);

    @Query(value = "SELECT * FROM user where email_address = :emailAddress and is_email_address_verified = true",
            nativeQuery = true)
    Optional<User> findOneByEmailAddressVerified(String emailAddress);
    @Query(value = "SELECT * FROM user u WHERE (:ids IS NULL OR u.id IN :ids) AND (u.display_name like %:search% OR u.email_address like %:search%)",nativeQuery = true)
    Page<User> findAllByIdIn(List<String> ids,String search, Pageable pageable);

    Optional<User> findOneByUsernameOrEmailAddressAndIsEmailAddressVerified(String loginName, String emailAddress, Boolean isEmailAddressVerified);
}
