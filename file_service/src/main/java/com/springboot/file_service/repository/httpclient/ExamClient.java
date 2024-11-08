package com.springboot.file_service.repository.httpclient;

import com.springboot.file_service.configuration.AuthenticationRequestInterceptor;
import com.springboot.file_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name="exam-service",url = "${app.services.exam}", configuration = {AuthenticationRequestInterceptor.class})
public interface ExamClient {
    @PostMapping(value = "/question-group/import/questions/{questionGroupId}",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    ApiResponse<?> importQuestionsIntoQuestionGroup(@RequestPart MultipartFile file, @PathVariable(name = "questionGroupId") Long questionGroupId) ;

    @PostMapping(value = "/subject/import/students/{subjectId}",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    ApiResponse<?> importStudentsIntoSubject(@RequestPart MultipartFile file, @PathVariable(name = "subjectId") Long subjectId) ;
}
