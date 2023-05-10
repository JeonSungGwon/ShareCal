package com.example.Capstone.dto;

import com.example.Capstone.entity.Schedule;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ScheduleDto {
    private Long id;
    private String title;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDateTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime;

    private Long memberId;

    public ScheduleDto() {
        this.id = null;
        this.title = "";
        this.startDateTime = LocalDateTime.now();
        this.endDateTime = LocalDateTime.now().plusHours(1);
    }

    public  ScheduleDto(String title, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }


    public static ScheduleDto from(Schedule schedule) {
        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setId(schedule.getId());
        scheduleDto.setTitle(schedule.getTitle());
        scheduleDto.setStartDateTime(schedule.getStartDateTime());
        scheduleDto.setEndDateTime(schedule.getEndDateTime());
        if (schedule.getMember() != null) {
            scheduleDto.setMemberId(schedule.getMember().getId());
        }
        return scheduleDto;
    }
}
