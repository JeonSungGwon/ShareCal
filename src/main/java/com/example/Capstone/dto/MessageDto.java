package com.example.Capstone.dto;

import com.example.Capstone.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String title;
    private String content;
    private String senderName;
    private String receiverName;

    public static MessageDto toDto(Message message) {
        return new MessageDto(
                message.getTitle(),
                message.getContent(),
                message.getSender().getNickname(),
                message.getReceiver().getNickname()
        );
    }
}
