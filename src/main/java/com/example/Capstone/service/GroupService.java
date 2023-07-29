package com.example.Capstone.service;

import com.example.Capstone.config.SecurityUtil;
import com.example.Capstone.dto.GroupDto;
import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.entity.*;
import com.example.Capstone.repository.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GroupService {

    private final ModelMapper modelMapper;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupScheduleService groupScheduleService;
    private final GroupMessageRepository groupMessageRepository;
    private final MemberGroupRepository memberGroupRepository;

    private final MemberService memberService;

    public GroupService(GroupRepository groupRepository, MemberRepository memberRepository, ModelMapper modelMapper,
                        MemberService memberService, GroupScheduleRepository groupScheduleRepository,
                        GroupScheduleService groupScheduleService, GroupMessageRepository groupMessageRepository,
                        MemberGroupRepository memberGroupRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        this.memberService = memberService;
        this.groupScheduleRepository = groupScheduleRepository;
        this.groupScheduleService = groupScheduleService;
        this.groupMessageRepository = groupMessageRepository;
        this.memberGroupRepository = memberGroupRepository;
    }

    @Transactional
    public Long createGroup(GroupDto groupDto) {
        MyGroup myGroup = MyGroup.builder()
                .name(groupDto.getName())
                .build();
        String sharedCode = RandomStringUtils.randomAlphanumeric(8);
        Long ownerId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(ownerId).orElse(null);

        if (member != null) {
            MemberGroup memberGroup = new MemberGroup();
            memberGroup.setMember(member);
            memberGroup.setGroup(myGroup);
            myGroup.getMemberGroups().add(memberGroup);

            myGroup.setOwner(member);
            myGroup.setSharedCode(sharedCode);
            memberGroupRepository.save(memberGroup);
            return groupRepository.save(myGroup).getId();
        }

        return null;
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        MyGroup myGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupId));
        List<GroupSchedule> schedulesToRemove = myGroup.getGroupSchedules(); // 예시: 그룹과 스케줄은 일대다 관계라고 가정
        for (GroupSchedule schedule : schedulesToRemove) {
            groupScheduleService.deleteGroupSchedule(schedule.getId());
        }
        List<GroupMessage> messagesToRemove = groupMessageRepository.findByGroup(myGroup);
        for (GroupMessage message : messagesToRemove) {
            groupMessageRepository.delete(message);
        }

        groupRepository.delete(myGroup);
    }

    @Transactional
    public GroupDto addMemberToGroup(String sharedCode, String email) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member owner = memberRepository.findById(myInfoBySecurity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        MyGroup myGroup = groupRepository.findBySharedCode(sharedCode);

        // 승인하는 사람이 그룹의 오너인지 확인
        if (!myGroup.getOwner().equals(owner)) {
            throw new IllegalStateException("Only the owner can approve group requests.");
        }

        Member member = memberRepository.findByEmail(email).orElse(null);
        if (member != null) {
            // 중복 체크: 이미 해당 그룹에 속한 멤버인지 확인
            boolean isMemberInGroup = memberGroupRepository.existsByGroupAndMember(myGroup, member);
            if (isMemberInGroup) {
                throw new IllegalStateException("The member is already in the group.");
            }

            MemberGroup memberGroup = new MemberGroup();
            memberGroup.setMember(member);
            memberGroup.setGroup(myGroup);
            myGroup.getMemberGroups().add(memberGroup);
            memberGroupRepository.save(memberGroup);

            MyGroup savedGroup = groupRepository.save(myGroup);
            return modelMapper.map(savedGroup, GroupDto.class);
        } else {
            throw new EntityNotFoundException("Member not found");
        }
    }



    @Transactional
    public void removeMemberFromGroup(Long groupId, Long memberId) {
        MyGroup myGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));

        List<MemberGroup> memberGroups = myGroup.getMemberGroups();
        MemberGroup memberGroupToRemove = null;

        // 해당 멤버를 가진 MemberGroup 엔티티를 찾아 제거
        for (MemberGroup memberGroup : memberGroups) {
            if (memberGroup.getMember().equals(member)) {
                memberGroupToRemove = memberGroup;
                break;
            }
        }

        if (memberGroupToRemove != null) {
            memberGroups.remove(memberGroupToRemove);
            groupRepository.save(myGroup);
        }
    }


    public GroupDto getGroup(Long groupId) {
        MyGroup myGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupId));
        System.out.println(myGroup.getName());
        return modelMapper.map(myGroup, GroupDto.class);
    }

}
