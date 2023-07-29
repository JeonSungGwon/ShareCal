package com.example.Capstone.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    private Long id;
    private String text;
    private Long scheduleId;
    private Long groupScheduleId;
    private String memberNickname;
    private Long memberId;

    // Constructors, getters, and setters
}
