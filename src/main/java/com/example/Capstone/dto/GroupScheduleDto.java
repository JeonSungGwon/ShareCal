package com.example.Capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.Group;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class GroupScheduleDto {

    private Long id;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long groupId;

    public GroupScheduleDto() {
        this.id = null;
        this.title = "";
        this.startDateTime = LocalDateTime.now();
        this.endDateTime = LocalDateTime.now().plusHours(1);
    }
}
