package com.example.Capstone.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ScheduleDto {
    private Long id;
    private String title;
    private String content;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime alarmDateTime;

    private boolean alarm;

    private List<ImageDto> images;
    private Long memberId;

    public ScheduleDto() {
        this.id = null;
        this.title = "";
        this.content = "";
        this.startDateTime = LocalDateTime.now();
        this.endDateTime = LocalDateTime.now().plusHours(1);
    }

    public  ScheduleDto(String title, String content, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

}
