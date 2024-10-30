package com.spring.boot.notification_service.repository;

import com.spring.boot.notification_service.entity.NotificationMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationMessageRepository extends MongoRepository<NotificationMessage, String> {
}
