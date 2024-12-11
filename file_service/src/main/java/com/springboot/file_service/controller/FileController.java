package com.springboot.file_service.controller;

import com.springboot.file_service.dto.request.ExportStudentOfClassRequest;
import com.springboot.file_service.dto.request.UserRequest;
import com.springboot.file_service.dto.response.ApiResponse;
import com.springboot.file_service.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class FileController {
    FileService fileService;
    @PostMapping(value = "/upload",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> saveFile(@RequestPart MultipartFile file, @RequestParam String parentId, @RequestParam  String parentType) {
        return ApiResponse.builder().data(fileService.saveFile(file,parentId,parentType)).build();
    }


    @PostMapping(value = "/export/student_of_class")
    public ResponseEntity<InputStreamResource> exportStudentOfClass(@RequestBody ExportStudentOfClassRequest dataExport, @RequestParam String typeExport) {
//         = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String fileName=dataExport.getClassName().replace(" ","")+".xlsx";
        ByteArrayInputStream inputStream=fileService.exportStudentOfClass(dataExport,typeExport);
        InputStreamResource resource = new InputStreamResource(inputStream);
        ResponseEntity<InputStreamResource> response=ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
        return response;
    }
    @PostMapping(value = "/export/student_verified")
    public ResponseEntity<InputStreamResource> exportStudentsVerified(@RequestBody List<UserRequest> dataExport, @RequestParam String typeExport) {
//         = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String fileName="StudentVerified"+".xlsx";
        ByteArrayInputStream inputStream=fileService.exportStudentsVerified(dataExport,typeExport);
        InputStreamResource resource = new InputStreamResource(inputStream);
        ResponseEntity<InputStreamResource> response=ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
        return response;
    }

    @PostMapping(value = "/question-group/import/questions/{questionGroupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> importStudentsIntoQuestionGroup(@RequestPart MultipartFile file, @PathVariable(name = "questionGroupId") Long questionGroupId) {
        return fileService.importQuestionsIntoQuestionGroup(file,questionGroupId);
    }

    @PostMapping(value = "/subject/import/student/{subjectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> importStudentsIntoSubject(@RequestPart MultipartFile file, @PathVariable(name = "subjectId") Long subjectId) {
        return fileService.importStudentsIntoSubject(file,subjectId);
    }
}
