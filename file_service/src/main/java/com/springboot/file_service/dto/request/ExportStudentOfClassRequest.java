package com.springboot.file_service.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportStudentOfClassRequest {
    List<UserRequest> userRequests;
    Long idClass;
    String className;
}
