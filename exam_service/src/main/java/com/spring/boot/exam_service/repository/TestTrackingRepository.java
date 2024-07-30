package com.spring.boot.exam_service.repository;

import com.spring.boot.exam_service.entity.TestTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestTrackingRepository extends JpaRepository<TestTracking,Long> {

    Optional<TestTracking> findByMultipleChoiceTestIdAndUserProfileUserID(Long testId, Long studentId);


}
