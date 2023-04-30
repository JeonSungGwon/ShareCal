package com.example.Capstone.api;

import com.example.Capstone.dto.GroupDto;
import com.example.Capstone.dto.MemberDto;
import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.MyGroup;
import com.example.Capstone.entity.Schedule;
import com.example.Capstone.entity.SharedSchedule;
import com.example.Capstone.repository.GroupRepository;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.service.GroupService;
import com.example.Capstone.service.MemberService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("")
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto groupDto) {
        Long groupId = groupService.createGroup(groupDto);
        System.out.println(groupId);
        GroupDto savedGroupDto = groupService.getGroup(groupId);
        System.out.println(savedGroupDto.getId());
        return ResponseEntity.ok(savedGroupDto);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupDto> addMemberToGroup(@PathVariable Long groupId, @RequestBody MemberDto memberDto) {
        groupService.addMemberToGroup(groupId, memberDto.getId());
        GroupDto savedGroupDto = groupService.getGroup(groupId);
        return ResponseEntity.ok(savedGroupDto);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Void> removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long memberId) {
        groupService.removeMemberFromGroup(groupId, memberId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable Long groupId) {
        GroupDto groupDto;
        try {
            groupDto = groupService.getGroup(groupId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(groupDto);
    }

    @GetMapping("/mygroups")
    public List<GroupDto> getMyGroup() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId()).orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<MyGroup> groups = member.getGroups(); // Member 엔티티에서 현재 사용자가 속한 그룹 리스트 가져오기
        System.out.println(groups.get(1).getId());


        return groups.stream().map(group -> new GroupDto(group.getId(), group.getName(), group.getMembers()))
                .collect(Collectors.toList());
    }



}