package com.spring.boot.exam_service.repository.httpclient;

import com.spring.boot.exam_service.configuration.AuthenticationRequestInterceptor;
import com.spring.boot.exam_service.dto.request.ExportStudentOfSubjectRequest;
import com.spring.boot.exam_service.dto.request.TrackEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.util.List;

@FeignClient(name="file-service",url = "${app.services.file}", configuration = {
    AuthenticationRequestInterceptor.class})
public interface FileClient {
    @PostMapping(value = "/export/student_of_class")
    ResponseEntity<Resource> exportStudentOfClass(@RequestBody ExportStudentOfSubjectRequest dataExport, @RequestParam String typeExport);
    @PostMapping(value = "/export/track_event")
    ResponseEntity<Resource> exportTrackEvent(@RequestBody List<TrackEventDTO> dataExport, @RequestParam String typeExport);
}
