package com.example.Capstone.service;

import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.MessageDto;
import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.entity.Image;
import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.Schedule;
import com.example.Capstone.entity.SharedSchedule;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.repository.ScheduleRepository;
import com.example.Capstone.repository.SharedScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service

@Transactional(readOnly = false)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private final MessageService messageService;

    private final SharedScheduleRepository sharedScheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, ModelMapper modelMapper, MemberRepository memberRepository,MemberService memberService, SharedScheduleRepository sharedScheduleRepository, MessageService messageService) {
        this.scheduleRepository = scheduleRepository;
        this.modelMapper = modelMapper;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.sharedScheduleRepository = sharedScheduleRepository;
        this.messageService=messageService;
    }

    public List<ScheduleDto> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return schedules.stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleDto.class))
                .collect(Collectors.toList());
    }

    public ScheduleDto getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such schedule"));
        return modelMapper.map(schedule, ScheduleDto.class);
    }

    @Transactional
    public ScheduleDto createSchedule(ScheduleDto scheduleDto) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        scheduleDto.setMemberId(myInfoBySecurity.getId());

        Schedule schedule = modelMapper.map(scheduleDto, Schedule.class);
        Member member = memberRepository.findById(scheduleDto.getMemberId()).orElse(null);
        schedule.setMember(member);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return modelMapper.map(savedSchedule, ScheduleDto.class);
    }

    @Transactional
    public ScheduleDto createSharedSchedule(ScheduleDto scheduleDto, List<Long> sharedWithIds) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        scheduleDto.setMemberId(myInfoBySecurity.getId());

        Schedule schedule = modelMapper.map(scheduleDto, Schedule.class);
        Member member = memberRepository.findById(scheduleDto.getMemberId()).orElse(null);
        schedule.setMember(member);

        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 공유 대상 멤버들에 대한 공유 스케줄 정보 저장
        List<Member> sharedWithMembers = memberRepository.findAllById(sharedWithIds);
        List<SharedSchedule> sharedSchedules = sharedWithMembers.stream().map(sharedWith -> {
            SharedSchedule sharedSchedule = new SharedSchedule();
            sharedSchedule.setSchedule(savedSchedule);
            sharedSchedule.setMember(sharedWith);

            sharedScheduleRepository.save(sharedSchedule); // 공유 스케줄 저장 후 아이디값이 생성됨

            // 공유받는 멤버에게 메시지 전송
            String messageTitle = "새로운 공유 스케줄이 도착했습니다.";
            String messageContent = "아래의 스케줄을 확인해주세요.";
            MessageDto messageDto = new MessageDto();
            messageDto.setSenderName(myInfoBySecurity.getNickname());
            messageDto.setReceiverName(sharedWith.getNickname());
            messageDto.setTitle(messageTitle);
            messageDto.setContent(messageContent);
            messageDto.setSharedScheduleId(sharedSchedule.getId()); // 공유 스케줄 아이디값 설정
            messageService.write(messageDto); // 메시지 전송

            return sharedSchedule;
        }).collect(Collectors.toList());



        return modelMapper.map(savedSchedule, ScheduleDto.class);
    }

    @Transactional
    public ScheduleDto updateSchedule(Long id, ScheduleDto scheduleDto, @RequestParam MultipartFile image)  {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such schedule"));

        if(scheduleDto.getTitle() != null && !scheduleDto.getTitle().isEmpty()) {
            schedule.setTitle(scheduleDto.getTitle());
        }
        if(scheduleDto.getStartDateTime() != null) {
            schedule.setStartDateTime(scheduleDto.getStartDateTime());
        }
        if(scheduleDto.getEndDateTime() != null) {
            schedule.setEndDateTime(scheduleDto.getEndDateTime());
        }
        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageData = image.getBytes();
                Image newImage = new Image();
                newImage.setImageData(imageData);
                newImage.setCreatedAt(LocalDateTime.now());
                newImage.setSchedule(schedule);
                schedule.getImages().add(newImage);
            }
            catch (IOException e){
                throw new RuntimeException("Failed to read image file", e);
            }
        }
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return modelMapper.map(updatedSchedule, ScheduleDto.class);
    }
    @Transactional
    public ScheduleDto updateImage(Long id, @RequestParam MultipartFile image)  {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such schedule"));
        if (image != null && !image.isEmpty()) {
            try {
                byte[] imageData = image.getBytes();
                Image newImage = new Image();
                newImage.setImageData(imageData);
                newImage.setCreatedAt(LocalDateTime.now());
                newImage.setSchedule(schedule);
                schedule.getImages().add(newImage);
            }
            catch (IOException e){
                throw new RuntimeException("Failed to read image file", e);
            }
        }
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return modelMapper.map(updatedSchedule, ScheduleDto.class);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

}