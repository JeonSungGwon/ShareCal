package com.example.Capstone.service;

import com.example.Capstone.dto.GroupScheduleDto;
import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.entity.GroupSchedule;
import com.example.Capstone.entity.Image;
import com.example.Capstone.entity.MyGroup;
import com.example.Capstone.entity.Schedule;
import com.example.Capstone.repository.GroupRepository;
import com.example.Capstone.repository.GroupScheduleRepository;
import com.example.Capstone.repository.ImageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class GroupScheduleService {

    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupRepository groupRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;
    private final MemberService memberService;

    public GroupScheduleService(GroupScheduleRepository groupScheduleRepository, GroupRepository groupRepository, ModelMapper modelMapper,
                                MemberService memberService,ImageRepository imageRepository) {
        this.groupScheduleRepository = groupScheduleRepository;
        this.groupRepository = groupRepository;
        this.modelMapper = modelMapper;
        this.memberService = memberService;
        this.imageRepository = imageRepository;
    }

    @Transactional
    public Long createGroupSchedule(GroupScheduleDto groupScheduleDto) {
        MyGroup myGroup = groupRepository.findById(groupScheduleDto.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupScheduleDto.getGroupId()));

        if (groupScheduleDto.isAlarm() && groupScheduleDto.getAlarmDateTime() == null) {
            throw new IllegalArgumentException("알람이 True인데 시간 설정이 안되었습니다.");
        }
        if (!groupScheduleDto.isAlarm() && groupScheduleDto.getAlarmDateTime() != null) {
            throw new IllegalArgumentException("알람이 False인데 시간 설정이 되어있습니다.");
        }

        GroupSchedule groupSchedule = GroupSchedule.builder()
                .title(groupScheduleDto.getTitle())
                .content(groupScheduleDto.getContent())
                .startDateTime(groupScheduleDto.getStartDateTime())
                .endDateTime(groupScheduleDto.getEndDateTime())
                .alarm(groupScheduleDto.isAlarm())
                .alarmDateTime(groupScheduleDto.getAlarmDateTime())
                .myGroup(myGroup)
                .build();

        return groupScheduleRepository.save(groupSchedule).getId();
    }


    @Transactional
    public GroupScheduleDto updateGroupSchedule(Long groupScheduleId,Long groupId, GroupScheduleDto groupScheduleDto, @RequestParam MultipartFile image) {
        GroupSchedule groupSchedule = groupScheduleRepository.findById(groupScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group schedule ID: " + groupScheduleId));
        MyGroup myGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupScheduleDto.getGroupId()));
        if(groupScheduleDto.getTitle() != null && !groupScheduleDto.getTitle().isEmpty()) {
            groupSchedule.setTitle(groupScheduleDto.getTitle());
        }
        if(groupScheduleDto.getContent() != null && !groupScheduleDto.getContent().isEmpty()) {
            groupSchedule.setContent(groupScheduleDto.getContent());
        }
        if(groupScheduleDto.getStartDateTime() != null) {
            groupSchedule.setStartDateTime(groupScheduleDto.getStartDateTime());
        }
        if(groupScheduleDto.getEndDateTime() != null) {
            groupSchedule.setEndDateTime(groupScheduleDto.getEndDateTime());
        }

        if(groupScheduleDto.getGroupId() != null) {
            groupSchedule.setMyGroup(myGroup); //가두리양식.
        }

        if(groupScheduleDto.getAlarmDateTime() != null){
            if(groupSchedule.isAlarm()){
                groupSchedule.setAlarmDateTime(groupScheduleDto.getAlarmDateTime());
            }
        }
        if(groupScheduleDto.isAlarm()!=groupSchedule.isAlarm()){
            groupSchedule.setAlarm(groupScheduleDto.isAlarm());
        }
        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageData = image.getBytes();
                Image newImage = new Image();
                newImage.setImageData(imageData);
                newImage.setCreatedAt(LocalDateTime.now());
                newImage.setGroupSchedule(groupSchedule);
                groupSchedule.getImages().add(newImage);
            }
            catch (IOException e){
                throw new RuntimeException("Failed to read image file", e);
            }
        }
        GroupSchedule upadtedGroupSchedule = groupScheduleRepository.save(groupSchedule);
        return modelMapper.map(upadtedGroupSchedule, GroupScheduleDto.class);
    }

    @Transactional
    public void deleteGroupSchedule(Long groupScheduleId) {
        GroupSchedule groupSchedule = groupScheduleRepository.findById(groupScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group schedule ID: " + groupScheduleId));

        List<Image> images = groupSchedule.getImages(); // 예시: 스케줄과 이미지는 일대다 관계라고 가정
        for (Image image : images) {
            imageRepository.delete(image);
        }
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
        groupScheduleDto.setAlarmDateTime(groupSchedule.getAlarmDateTime());
        groupScheduleDto.setAlarm(groupSchedule.isAlarm());
        return groupScheduleDto;
    }
}
