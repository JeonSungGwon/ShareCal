package com.example.Capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.Group;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GroupScheduleDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime alarmDateTime;

    private boolean alarm;

    private List<ImageDto> images;
    private Long groupId;

    public GroupScheduleDto() {
        this.id = null;
        this.title = "";
        this.startDateTime = LocalDateTime.now();
        this.endDateTime = LocalDateTime.now().plusHours(1);
    }
}
