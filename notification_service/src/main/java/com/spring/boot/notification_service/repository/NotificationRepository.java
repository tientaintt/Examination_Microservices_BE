package com.spring.boot.notification_service.repository;


import com.spring.boot.notification_service.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface NotificationRepository extends MongoRepository<Notification, String> {
    Page<Notification> findAllByUserId(String userId ,Pageable pageable);
}
