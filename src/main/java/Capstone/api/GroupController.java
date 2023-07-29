package Capstone.api;
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


    @PostMapping("")
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto groupDto) {
        Long groupId = groupService.createGroup(groupDto);
        System.out.println(groupId);
        GroupDto savedGroupDto = groupService.getGroup(groupId);
        System.out.println(savedGroupDto.getId());
        return ResponseEntity.ok(savedGroupDto);
    }

    @DeleteMapping("owner/{groupId}")
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
        Member member = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<MyGroup> ownedGroups = member.getOwnedGroups();
        List<MyGroup> memberGroups = member.getMemberGroups().stream()
                .map(MemberGroup::getGroup)
                .collect(Collectors.toList());
        List<GroupDto> groupDtos = new ArrayList<>();

        for (MyGroup group : ownedGroups) {
            List<Member> members = group.getMemberGroups().stream()
                    .map(MemberGroup::getMember)
                    .collect(Collectors.toList());
            GroupDto groupDto = new GroupDto(group.getId(), group.getName(), group.getOwner().getId(), members, group.getSharedCode());
            groupDtos.add(groupDto);
        }

        for (MyGroup group : memberGroups) {
            if (!ownedGroups.contains(group)) {
                List<Member> members = group.getMemberGroups().stream()
                        .map(MemberGroup::getMember)
                        .collect(Collectors.toList());
                GroupDto groupDto = new GroupDto(group.getId(), group.getName(), group.getOwner().getId(), members, group.getSharedCode());
                groupDtos.add(groupDto);
            }
        }

        return groupDtos;
    }
}
