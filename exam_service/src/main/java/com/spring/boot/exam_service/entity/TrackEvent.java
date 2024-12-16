package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "track_event")
public class TrackEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private Double x;
    private Double y;
    private String key;
    private String visibility;
    private Date timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    private TestTracking testTracking;

}
