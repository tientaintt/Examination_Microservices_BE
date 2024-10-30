package com.springboot.file_service.entity;

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
public abstract class IBaseEntity {
    @Id
    private String id;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
    private String createdBy;
    private String updatedBy;
    private Boolean isDeleted = false;
}