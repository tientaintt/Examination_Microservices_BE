package com.spring.boot.exam_service.repository;


import com.spring.boot.exam_service.entity.TestTracking;
import com.spring.boot.exam_service.entity.TrackEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackEventRepository extends JpaRepository<TrackEvent,Long> {

        List<TrackEvent> findAllByTestTracking(TestTracking testTracking);


}
