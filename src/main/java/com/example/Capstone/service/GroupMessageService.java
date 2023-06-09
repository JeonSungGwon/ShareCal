package com.example.Capstone.service;

import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.MessageDto;
import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.Message;
import com.example.Capstone.entity.SharedSchedule;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.repository.MessageRepository;
import com.example.Capstone.repository.SharedScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GroupMessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final SharedScheduleRepository sharedScheduleRepository;

    private final MemberService memberService;

    @Transactional
    public MessageDto write(MessageDto messageDto) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member sender = memberRepository.findByNickname(myInfoBySecurity.getNickname());
        Member receiver = memberRepository.findByNickname(messageDto.getReceiverName());
        messageDto.setSenderName(sender.getNickname());


        Message message = new Message();
        message.setReceiver(receiver);
        message.setSender(sender);

        SharedSchedule sharedSchedule = sharedScheduleRepository.findById(messageDto.getSharedScheduleId()).orElse(null);
        System.out.println(messageDto.getSharedScheduleId());
        message.setTitle(messageDto.getTitle());
        message.setContent(messageDto.getContent());
        message.setSharedSchedule(sharedSchedule);
        System.out.println(sharedSchedule);
        message.setDeletedByReceiver(false);
        message.setDeletedBySender(false);
        messageRepository.save(message);

        return MessageDto.toDto(message);
    }


    @Transactional(readOnly = true)
    public List<MessageDto> receivedMessage(Member member) {
        // 받은 편지함 불러오기
        // 한 명의 유저가 받은 모든 메시지
        // 추후 JWT를 이용해서 재구현 예정
        List<Message> messages = messageRepository.findAllByReceiver(member);
        List<MessageDto> messageDtos = new ArrayList<>();

        for(Message message : messages) {
            // message 에서 받은 편지함에서 삭제하지 않았으면 보낼 때 추가해서 보내줌
            if(!message.isDeletedByReceiver()) {
                messageDtos.add(MessageDto.toDto(message));
            }
        }
        return messageDtos;
    }

    // 받은 편지 삭제
    @Transactional
    public Object deleteMessageByReceiver(int id, Member user) {
        Message message = messageRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        });

        if(user == message.getSender()) {
            message.deleteByReceiver(); // 받은 사람에게 메시지 삭제
            if (message.isDeleted()) {
                // 받은사람과 보낸 사람 모두 삭제했으면, 데이터베이스에서 삭제요청
                messageRepository.delete(message);
                return "양쪽 모두 삭제";
            }
            return "한쪽만 삭제";
        } else {
            return new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }
    }



    @Transactional(readOnly = true)
    public List<MessageDto> sentMessage(Member member) {
        // 보낸 편지함 불러오기
        // 한 명의 유저가 받은 모든 메시지
        // 추후 JWT를 이용해서 재구현 예정
        List<Message> messages = messageRepository.findAllBySender(member);
        List<MessageDto> messageDtos = new ArrayList<>();

        for(Message message : messages) {
            // message 에서 받은 편지함에서 삭제하지 않았으면 보낼 때 추가해서 보내줌
            if(!message.isDeletedBySender()) {
                messageDtos.add(MessageDto.toDto(message));
            }
        }
        return messageDtos;
    }


    // 보낸 편지 삭제
    @Transactional
    public Object deleteMessageBySender(int id, Member member) {
        Message message = messageRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        });

        if(member == message.getSender()) {
            message.deleteBySender(); // 받은 사람에게 메시지 삭제
            if (message.isDeleted()) {
                // 받은사람과 보낸 사람 모두 삭제했으면, 데이터베이스에서 삭제요청
                messageRepository.delete(message);
                return "양쪽 모두 삭제";
            }
            return "한쪽만 삭제";
        } else {
            return new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }


    }
}