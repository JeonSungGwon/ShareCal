package com.example.Capstone.api;

import com.example.Capstone.dto.GroupDto;
import com.example.Capstone.dto.GroupMessageDto;
import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.entity.GroupMessage;
import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.MyGroup;
import com.example.Capstone.repository.GroupMessageRepository;
import com.example.Capstone.repository.GroupRepository;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.service.GroupService;
import com.example.Capstone.service.MemberService;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GroupMessageController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final GroupService groupService;
    private final GroupMessageRepository groupMessageRepository;

    public GroupMessageController(MemberService memberService, MemberRepository memberRepository,
                           GroupRepository groupRepository, GroupMessageRepository groupMessageRepository, GroupService groupService) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.groupService = groupService;
    }

    @PostMapping("/group/accept")
    public ResponseEntity<String> acceptGroupRequest(@RequestParam String sharedCode) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        // 공유코드에 해당하는 그룹 검색
        MyGroup group = groupRepository.findBySharedCode(sharedCode);

        // 공유코드를 입력한 멤버가 그룹에 속해있는지 확인
        if (group.getMembers().contains(member)) {
            return ResponseEntity.badRequest().body("You are already a member of this group.");
        }
        Member groupOwner = group.getOwner();

        // 오너에게 메시지 보내기
        String message = "User with shared code " + sharedCode + " wants to join your group. Accept or decline?";
        GroupMessage groupMessage = new GroupMessage(message, group, groupOwner);
        groupMessageRepository.save(groupMessage);

        return ResponseEntity.ok("Group request sent to the owner for approval.");
    }
    @GetMapping("/group/messages")
    public List<GroupMessageDto> getGroupMessages() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<GroupMessage> messages = groupMessageRepository.findByOwner(member);
        return messages.stream()
                .map(message -> new GroupMessageDto(message.getId(), message.getMessage(), message.getGroup().getId(), message.getOwner().getId()))
                .collect(Collectors.toList());
    }


    @GetMapping("/accept/message")
    public GroupDto acceptGroupRequest(@RequestParam String sharedCode, @RequestParam String email) {
        // 그룹 멤버를 추가하고 승인하는 로직 수행
        GroupDto groupDto = groupService.addMemberToGroup(sharedCode, email);

        // 그룹 멤버 추가 후의 결과 반환
        return groupDto;
    }


}

