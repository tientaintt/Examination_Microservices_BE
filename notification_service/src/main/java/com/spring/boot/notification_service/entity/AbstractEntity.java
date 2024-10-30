package com.spring.boot.notification_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AbstractEntity {
    @Id
    private String id;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
    private String createdBy;
    private String updatedBy;
    private Boolean isDeleted = false;
}
