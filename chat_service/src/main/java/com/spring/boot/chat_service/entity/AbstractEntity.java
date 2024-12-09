package com.spring.boot.chat_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document
public abstract class AbstractEntity {
    @Id
    private String id;
    @CreatedDate
    private Date createdAt = new Date();
    @LastModifiedDate
    private Date updatedAt = new Date();
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;

}
