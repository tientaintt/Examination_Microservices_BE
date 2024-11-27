package com.spring.boot.notification_service.repository;


import com.spring.boot.notification_service.entity.Notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


import java.util.List;


public interface NotificationRepository extends MongoRepository<Notification, String> {
    //    Page<Notification> getAllByReceiverIdOrderByCreatedAtDesc(String userId , Pageable pageable);
    @Query(value = "{ 'receiverId': ?0 }")
    Page<Notification> findByReceiverId(String userId, Pageable pageable);

}

