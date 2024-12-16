package com.spring.boot.exam_service.service;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.TrackEventDTO;
import com.spring.boot.exam_service.entity.TrackEvent;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TrackEventService {
    ApiResponse<?> addTrackEvent(Long testTrackingId, List<TrackEventDTO> trackEventDTOs);

    ResponseEntity<Resource> exportEventLogDoExam(Long scoreId, String typeExport);
}
