package com.example.Capstone.dto;

import com.example.Capstone.entity.Image;
import com.example.Capstone.entity.Schedule;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


    public static ScheduleDto from(Schedule schedule) {
        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setId(schedule.getId());
        scheduleDto.setTitle(schedule.getTitle());
        scheduleDto.setContent(schedule.getContent());
        scheduleDto.setStartDateTime(schedule.getStartDateTime());
        scheduleDto.setEndDateTime(schedule.getEndDateTime());
        if (schedule.getMember() != null) {
            scheduleDto.setMemberId(schedule.getMember().getId());
        }
        return scheduleDto;
    }

}
