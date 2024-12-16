package com.spring.boot.exam_service.service.impl;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.TrackEventDTO;
import com.spring.boot.exam_service.entity.Score;
import com.spring.boot.exam_service.entity.TestTracking;
import com.spring.boot.exam_service.entity.TrackEvent;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.ScoreRepository;
import com.spring.boot.exam_service.repository.TestTrackingRepository;
import com.spring.boot.exam_service.repository.TrackEventRepository;
import com.spring.boot.exam_service.repository.httpclient.FileClient;
import com.spring.boot.exam_service.service.TrackEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TrackEventServiceImpl implements TrackEventService {

    private final TestTrackingRepository testTrackingRepository;
    private final TrackEventRepository trackEventRepository;
    private final ScoreRepository scoreRepository;
    private final FileClient fileClient;

    @Override
    public ApiResponse<?> addTrackEvent(Long testTrackingId, List<TrackEventDTO> trackEventDTOs) {
        Optional<TestTracking> testTracking=testTrackingRepository.findById(testTrackingId);
        if(testTracking.isPresent()){
            trackEventDTOs.forEach(trackEventDTO->{
                TrackEvent trackEvent=TrackEvent.builder()
                        .x(trackEventDTO.getX())
                        .y(trackEventDTO.getY())
                        .key(trackEventDTO.getKey())
                        .type(trackEventDTO.getType())
                        .visibility(trackEventDTO.getVisibility())
                        .timestamp(trackEventDTO.getTimestamp())
                        .testTracking(testTracking.get())
                        .build();
                trackEventRepository.save(trackEvent);
            });
            return ApiResponse.builder().build();
        }else {
            throw new AppException(ErrorCode.CANNOT_FIND_TEST_TRACKING);
        }

    }

    @Override
    public ResponseEntity<Resource> exportEventLogDoExam(Long scoreId, String typeExport) {
        log.info("exportEventLogDoExam");
        Optional<Score> score = scoreRepository.findById(scoreId);
        if (score.isEmpty()) {
            throw new AppException(ErrorCode.SCORE_NOT_FOUND_ERROR);
        }
        Optional<TestTracking> testTracking=testTrackingRepository.findByMultipleChoiceTestIdAndUserID(score.get().getMultipleChoiceTest().getId(),score.get().getUserID());
        List<TrackEvent> trackEvents=trackEventRepository.findAllByTestTracking(testTracking.get());
        List<TrackEventDTO> trackEventDTOS=trackEvents.stream().map(trackEvent ->TrackEventDTO.builder()
                .x(trackEvent.getX())
                .y(trackEvent.getY())
                .key(trackEvent.getKey())
                .type(trackEvent.getType())
                .visibility(trackEvent.getVisibility())
                .timestamp(trackEvent.getTimestamp())
                .build()).toList();
        return fileClient.exportTrackEvent(trackEventDTOS,typeExport);
    }
}
