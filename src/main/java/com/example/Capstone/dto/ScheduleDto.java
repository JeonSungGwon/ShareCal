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
    private String backgroundColor;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime alarmDateTime;

    private boolean alarm;

    private List<ImageDto> images;
    private Long memberId;

    public ScheduleDto() {
        this.id = null;
        this.title = "";
        this.content = "";
        this.start = LocalDateTime.now();
        this.end = LocalDateTime.now().plusHours(1);
    }

    public  ScheduleDto(String title, String content, LocalDateTime start, LocalDateTime end){
        this.title = title;
        this.content = content;
        this.start = start;
        this.end = end;
    }

}
