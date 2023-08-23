package com.example.Capstone.api;

import com.example.Capstone.dto.GroupDto;
import com.example.Capstone.dto.GroupMessageDto;
import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.entity.*;
import com.example.Capstone.repository.GroupMessageRepository;
import com.example.Capstone.repository.GroupRepository;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.service.GroupService;
import com.example.Capstone.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = "그룹 메시지")
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

    @PostMapping("/group/accept") //그룹 참여 요청 메시지
    @Operation(summary = "그룹 참여 요청 메시지 전송")
    public ResponseEntity<String> acceptGroupRequest(@RequestParam String sharedCode) {
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
    @GetMapping("/group/messages")
    @Operation(summary = "모든 그룹 메시지 불러오기")
    public List<GroupMessageDto> getGroupMessages() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<GroupMessage> messages = groupMessageRepository.findByOwner(member);
        return messages.stream()
                .map(message -> new GroupMessageDto(message.getId(), message.getMessage(), message.getGroup().getId(), message.getSender().getEmail(), message.getSender().getNickname(), message.getOwner().getId(), message.getGroup().getSharedCode()))
                .collect(Collectors.toList());
    }

    @GetMapping("/accept/message/{id}")  // 메시지로 온 그룹 요청 승인
    @Operation(summary = "메시지로 온 그룹 요청 승인")
    public GroupDto acceptGroupRequest(@PathVariable Long id ,@RequestParam String sharedCode, @RequestParam String email) {
        // 그룹 멤버를 추가하고 승인하는 로직 수행
        GroupDto groupDto = groupService.addMemberToGroup(sharedCode, email);

        GroupMessage groupMessage = groupMessageRepository.findById(id).orElse(null);
        groupMessageRepository.delete(groupMessage);
        // 그룹 멤버 추가 후의 결과 반환
        return groupDto;
    }

    @DeleteMapping("/reject/message/{id}")
    @Operation(summary = "메시지로 온 그룹 요청 거부")
    public ResponseEntity<String> disApproveGroupRequest(@PathVariable Long id){
        GroupMessage groupMessage = groupMessageRepository.findById(id).orElse(null);
        groupMessageRepository.delete(groupMessage);
        return ResponseEntity.ok("Shared Schedule disapproved successfully");
    }


}

