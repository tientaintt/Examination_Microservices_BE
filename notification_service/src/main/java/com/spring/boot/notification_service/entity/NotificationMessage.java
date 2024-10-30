package com.spring.boot.notification_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collation = "notification_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotificationMessage implements Serializable {
    private String type;
    private String image;
    private String title;
    private Long idOfTypeNotify;
}
