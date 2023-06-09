package com.example.Capstone.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupMessageDto {
    private Long id;
    private String message;
    private Long groupId;
    private Long ownerId;

    public GroupMessageDto(Long id, String message, Long groupId, Long ownerId) {
        this.id = id;
        this.message = message;
        this.groupId = groupId;
        this.ownerId = ownerId;
    }
}
