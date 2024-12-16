package com.springboot.file_service.dto.request;



import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TrackEventRequest {
    private String type;
    private Double x;
    private Double y;
    private String key;
    private String visibility;
    private Date timestamp;

    @Override
    public String toString() {
        return "Event {" +
                "type='" + type + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", key='" + key + '\'' +
                ", visibility='" + visibility + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

}
