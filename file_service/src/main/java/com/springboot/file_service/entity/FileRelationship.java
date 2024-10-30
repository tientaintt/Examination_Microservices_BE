package com.springboot.file_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "file_relationship")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FileRelationship extends IBaseEntity{
    private String parentId;
    private String parentType;
    private String fileId;
    private String mimeType;
    private String name;
    private Long size;
    private Long duration;
    private String webViewLink;
    private String pathFile;
}