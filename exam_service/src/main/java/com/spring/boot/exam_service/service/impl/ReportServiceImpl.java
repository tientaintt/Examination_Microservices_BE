package com.spring.boot.exam_service.service.impl;

import com.spring.boot.exam_service.controller.StudentController;
import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.dto.response.ReportTestByMonthResponse;
import com.spring.boot.exam_service.dto.response.ReportTotalResponse;
import com.spring.boot.exam_service.repository.MultipleChoiceTestRepository;
import com.spring.boot.exam_service.repository.SubjectRepository;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.service.ReportService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {

    private final IdentityService identityService;
    private final MultipleChoiceTestRepository multipleChoiceTestRepository;
    private final SubjectRepository subjectRepository;
    @Override
    public ApiResponse<?> reportTestByMonthByUserId() {
        String  myId =identityService.getCurrentUser().getId();
        List<ReportTestByMonthResponse> response=multipleChoiceTestRepository.countTestsByMonthByCreateBy(myId);
        log.info(String.valueOf(response.getFirst().getMonth()));
        return ApiResponse.builder().data(response).build();
    }
    @Override
    public ApiResponse<?> reportTotal() {
        UserRequest currentUser = identityService.getCurrentUser();
       int totalStudents= identityService.getTotalStudents();
       int totalTests= multipleChoiceTestRepository.countByCreatedBy(currentUser.getId());
       int totalSubjects= subjectRepository.countByCreatedBy(currentUser.getId());
        return ApiResponse.builder().data(ReportTotalResponse.builder()
                        .totalStudent(totalStudents)
                        .totalSubjects(totalSubjects)
                        .totalTests(totalTests)
                        .build())
                .build();
    }
}
