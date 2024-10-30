package com.springboot.file_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRelationshipDTO {
    private String id;
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("parent_type")
    private String parentType;
    @JsonProperty("file_id")
    private String fileId;
    @JsonProperty("path_file")
    private String pathFile;
    @JsonProperty("mime_type")
    private String mimeType;
    private String name;
    private Long size;
    private Long duration;
    @JsonProperty("web_view_link")
    private String webViewLink;
}