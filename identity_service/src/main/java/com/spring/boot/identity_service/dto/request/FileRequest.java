package com.spring.boot.identity_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRequest {
    private String id;
    private String name;
    private int size;
    private String duration;
    private String parent_id;
    private String parent_type;
    private String file_id;
    private String path_file;
    private String mime_type;
    private String web_view_link;
}
