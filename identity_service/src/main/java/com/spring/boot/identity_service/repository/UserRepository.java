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
public interface UserRepository extends JpaRepository< User,String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
    @Query(value = "SELECT * FROM user where username = :loginName",
            nativeQuery = true)
    Optional<User> findOneByUsername(String loginName);

    @Query(value = "SELECT * FROM user where email_address = :emailAddress and is_email_address_verified = true",
            nativeQuery = true)
    Optional<User> findOneByEmailAddressVerified(String emailAddress);

    Page<User> findAllByIdIn(List<String> ids, Pageable pageable);

}
