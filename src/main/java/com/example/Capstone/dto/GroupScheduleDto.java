package com.example.Capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GroupScheduleDto {

    private Long id;
    private String title;
    private String content;
    private String backgroundColor;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime alarmDateTime;

    private boolean alarm;

    private List<ImageDto> images;
    private Long groupId;

    public GroupScheduleDto() {
        this.id = null;
        this.title = "";
        this.start = LocalDateTime.now();
        this.end = LocalDateTime.now().plusMinutes(1);
    }
}
