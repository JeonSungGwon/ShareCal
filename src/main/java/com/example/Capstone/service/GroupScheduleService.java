package com.example.Capstone.service;

import com.example.Capstone.dto.GroupScheduleDto;
import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.entity.GroupSchedule;
import com.example.Capstone.entity.MyGroup;
import com.example.Capstone.entity.Schedule;
import com.example.Capstone.repository.GroupRepository;
import com.example.Capstone.repository.GroupScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class GroupScheduleService {

    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupRepository groupRepository;
    private final ModelMapper modelMapper;
    private final MemberService memberService;

    public GroupScheduleService(GroupScheduleRepository groupScheduleRepository, GroupRepository groupRepository, ModelMapper modelMapper, MemberService memberService) {
        this.groupScheduleRepository = groupScheduleRepository;
        this.groupRepository = groupRepository;
        this.modelMapper = modelMapper;
        this.memberService = memberService;
    }

    @Transactional
    public Long createGroupSchedule(GroupScheduleDto groupScheduleDto) {
        MyGroup myGroup = groupRepository.findById(groupScheduleDto.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupScheduleDto.getGroupId()));

        GroupSchedule groupSchedule = GroupSchedule.builder()
                .title(groupScheduleDto.getTitle())
                .startDateTime(groupScheduleDto.getStartDateTime())
                .endDateTime(groupScheduleDto.getEndDateTime())
                .myGroup(myGroup)
                .build();

        return groupScheduleRepository.save(groupSchedule).getId();
    }

    @Transactional
    public GroupScheduleDto updateGroupSchedule(Long groupScheduleId, GroupScheduleDto groupScheduleDto) {
        GroupSchedule groupSchedule = groupScheduleRepository.findById(groupScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group schedule ID: " + groupScheduleId));
        if(groupScheduleDto.getTitle() != null && !groupScheduleDto.getTitle().isEmpty()) {
            groupSchedule.setTitle(groupScheduleDto.getTitle());
        }
        if(groupScheduleDto.getStartDateTime() != null) {
            groupSchedule.setStartDateTime(groupScheduleDto.getStartDateTime());
        }
        if(groupScheduleDto.getEndDateTime() != null) {
            groupSchedule.setEndDateTime(groupScheduleDto.getEndDateTime());
        }
        GroupSchedule upadtedGroupSchedule = groupScheduleRepository.save(groupSchedule);
        return modelMapper.map(upadtedGroupSchedule, GroupScheduleDto.class);
    }

    @Transactional
    public void deleteGroupSchedule(Long groupScheduleId) {
        GroupSchedule groupSchedule = groupScheduleRepository.findById(groupScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group schedule ID: " + groupScheduleId));

        groupScheduleRepository.delete(groupSchedule);
    }

    public List<GroupScheduleDto> getGroupSchedules(Long groupId) {
        List<GroupSchedule> groupSchedules = groupScheduleRepository.findAllByMyGroup_Id(groupId);
        List<GroupScheduleDto> groupScheduleDtos = new ArrayList<>();
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Long memberId = myInfoBySecurity.getId();

        for (GroupSchedule groupSchedule : groupSchedules) {
            // 그룹에 속한 멤버인 경우에만 그룹 일정 목록에 추가
            if (groupSchedule.getMyGroup().getMembers().stream().anyMatch(m -> m.getId().equals(memberId))) {
                groupScheduleDtos.add(convertEntityToDto(groupSchedule));
            }
        }

        return groupScheduleDtos;
    }
    private GroupScheduleDto convertEntityToDto(GroupSchedule groupSchedule) {
        GroupScheduleDto groupScheduleDto = new GroupScheduleDto();
        groupScheduleDto.setId(groupSchedule.getId());
        groupScheduleDto.setTitle(groupSchedule.getTitle());
        groupScheduleDto.setStartDateTime(groupSchedule.getStartDateTime());
        groupScheduleDto.setEndDateTime(groupSchedule.getEndDateTime());
        groupScheduleDto.setGroupId(groupSchedule.getMyGroup().getId());
        return groupScheduleDto;
    }
}
