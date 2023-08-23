package com.example.Capstone.api;
import com.example.Capstone.entity.MemberGroup;

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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Api(tags = "그룹")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;


    @PostMapping("")
    @Operation(summary = "그룹 생성")
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto groupDto) {
        Long groupId = groupService.createGroup(groupDto);
        GroupDto savedGroupDto = groupService.getGroup(groupId);
        return ResponseEntity.ok(savedGroupDto);
    }

    @DeleteMapping("owner/{groupId}")
    @Operation(summary = "그룹 삭제", description = "owner만 삭제할 수 있음")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }

    //@PostMapping("/code")
    //public ResponseEntity<GroupDto> addMemberToGroup(@RequestBody String sharedCode) {
    //    GroupDto savedGroupDto = groupService.addMemberToGroup(sharedCode);
    //    return ResponseEntity.ok(savedGroupDto);
    // }

    @DeleteMapping("/{groupId}/members/{memberId}")
    @Operation(summary = "그룹 나가기")
    public ResponseEntity<Void> removeMemberFromGroup(@PathVariable Long groupId, @PathVariable Long memberId) {
        groupService.removeMemberFromGroup(groupId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/groups/{groupId}")
    @Operation(summary = "해당하는 그룹 가져오가")
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
    @Operation(summary = "자신이 소속된 그룹 모두 가져오기")
    public ResponseEntity<List<GroupDto>> getMyGroup() {
        List<GroupDto> groupDtos = groupService.getMyGroups();
        return ResponseEntity.ok(groupDtos);

    }
}
