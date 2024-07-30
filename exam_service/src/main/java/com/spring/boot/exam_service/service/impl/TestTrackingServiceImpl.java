package com.spring.boot.exam_service.service.impl;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.response.TestTrackingResponse;
import com.spring.boot.exam_service.entity.MultipleChoiceTest;
import com.spring.boot.exam_service.entity.TestTracking;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.MultipleChoiceTestRepository;
import com.spring.boot.exam_service.repository.TestTrackingRepository;
import com.spring.boot.exam_service.service.TestTrackingService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TestTrackingServiceImpl implements TestTrackingService {
    private final TestTrackingRepository testTrackingRepository;

    private final MultipleChoiceTestRepository multipleChoiceTestRepository;
    @Override
    public ApiResponse<?> getTestingInProgress(Long testId) {
//        Long  myId = webUtils.getCurrentLogedInUser().getUserID();
//        Optional<TestTracking> currentTest =
//                testTrackingRepository
//                        .findByMultipleChoiceTestIdAndUserProfileUserID(testId, myId);
//        if (currentTest.isEmpty()){
//            return ApiResponse.builder().build();
//        }
//        TestTrackingResponse response = CustomBuilder.buildTestTrackingResponse(currentTest.get());
        return ApiResponse.builder().build();
    }

    @Override
    public ApiResponse<?> createTestingInProcess(Long testId) {
//        UserProfile me = webUtils.getCurrentLogedInUser();
//        Optional<MultipleChoiceTest> multipleChoiceTestOp = multipleChoiceTestRepository.findById(testId);
//        if(multipleChoiceTestOp.isEmpty()) {
//            throw new AppException(ErrorCode.MULTIPLE_CHOICE_NOT_FOUND_ERROR);
//        }
//        MultipleChoiceTest multipleChoiceTest = multipleChoiceTestOp.get();
//        Long maxTestingTime = Timestamp.from(ZonedDateTime.now().toInstant().plus(
//                multipleChoiceTest.getTestingTime(),
//                ChronoUnit.MINUTES
//        )).getTime();
//        Long dueTime = Math.min(
//                multipleChoiceTest.getEndDate(),
//                maxTestingTime
//        );
//        TestTracking testTracking = TestTracking.builder()
//                .dueTime(dueTime)
//                .firstTimeAccess(Timestamp.from(ZonedDateTime.now().toInstant()).getTime())
//                .userProfile(me)
//                .multipleChoiceTest(multipleChoiceTest)
//                .build();
//        testTracking = testTrackingRepository.save(testTracking);
//        TestTrackingResponse response = CustomBuilder.buildTestTrackingResponse(testTracking);
        return ApiResponse.builder().build();
    }
}
