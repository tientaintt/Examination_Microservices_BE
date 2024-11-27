package com.spring.boot.notification_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Notification extends AbstractEntity implements Serializable {
    private String receiverId;
    private NotificationMessage message;
    private boolean read;
    private String senderId;
}
