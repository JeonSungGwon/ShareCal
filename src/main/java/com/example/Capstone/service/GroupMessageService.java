package com.example.Capstone.service;

import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.MessageDto;
import com.example.Capstone.entity.*;
import com.example.Capstone.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final GroupRepository groupRepository;
    private final GroupMessageRepository groupMessageRepository;

    private final MemberService memberService;

    @Transactional
    public MessageDto write(MessageDto messageDto) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member sender = memberRepository.findByNickname(myInfoBySecurity.getNickname());
        Member receiver = memberRepository.findByNickname(messageDto.getReceiverName());
        messageDto.setSenderName(sender.getNickname());


        Message message = new Message();
        message.setReceiver(receiver); //test
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
    public Object deleteMessageByReceiver(Long id, Member user) {
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
    public Object deleteMessageBySender(Long id, Member member) {
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
    public ResponseEntity<String> acceptGroupRequest(String sharedCode){
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        // 공유코드에 해당하는 그룹 검색
        MyGroup group = groupRepository.findBySharedCode(sharedCode);

        if(group==null){
            return ResponseEntity.badRequest().body("올바르지 않은 sharedCode");
        }

        // 공유코드를 입력한 멤버가 그룹에 속해있는지 확인
        if (group.getMemberGroups().contains(member)) {
            return ResponseEntity.badRequest().body("You are already a member of this group.");
        }
        Member groupOwner = group.getOwner();
        Member sender = memberRepository.findById(myInfoBySecurity.getId()).orElse(null);

        // 오너에게 메시지 보내기
        String message = "참여 요청합니다" + group.getName();
        GroupMessage groupMessage = new GroupMessage(message, group, groupOwner, sender);
        groupMessageRepository.save(groupMessage);

        return ResponseEntity.ok("그룹 가입 신청이 소유자에게 전송되었습니다.");
    }
}