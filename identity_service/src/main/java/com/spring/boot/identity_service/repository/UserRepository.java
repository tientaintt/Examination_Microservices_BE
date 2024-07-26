package com.spring.boot.identity_service.repository;

import com.spring.boot.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository< User,String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
    @Query(value = "SELECT * FROM user where user_name = :loginName",
            nativeQuery = true)
    Optional<User> findOneByUsername(String loginName);

}
