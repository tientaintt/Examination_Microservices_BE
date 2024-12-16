package com.spring.boot.exam_service.controller;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.TrackEventDTO;
import com.spring.boot.exam_service.service.TrackEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/track-event")
@Slf4j
@AllArgsConstructor

public class TrackEventController {
    private  final TrackEventService trackEventService;
    @PostMapping(value = "/create/{testTrackingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> createTrackEvent(@PathVariable(name = "testTrackingId") Long testTrackingId, @RequestBody List<TrackEventDTO> trackEventDTOs){
        return trackEventService.addTrackEvent(testTrackingId,trackEventDTOs);
    }
    @GetMapping(value = "/export/{scoreId}",produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Resource> exportEventLog(@PathVariable(name = "scoreId") Long scoreId,
                                                              @RequestParam(defaultValue = "excel") String typeExport){
        return trackEventService.exportEventLogDoExam(scoreId,typeExport);
    }
}
