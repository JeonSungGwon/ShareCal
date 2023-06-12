package com.example.Capstone.service;

import com.example.Capstone.config.SecurityUtil;
import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.MessageDto;
import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.entity.*;
import com.example.Capstone.repository.*;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = false)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private final MessageService messageServices;

    private final SharedScheduleRepository sharedScheduleRepository;

    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    final DefaultMessageService messageService;

    public ScheduleService(ScheduleRepository scheduleRepository, ModelMapper modelMapper, MemberRepository memberRepository,
                           MemberService memberService, SharedScheduleRepository sharedScheduleRepository, MessageService messageService,
                           ImageRepository imageRepository, CommentRepository commentRepository) {
        this.scheduleRepository = scheduleRepository;
        this.modelMapper = modelMapper;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.sharedScheduleRepository = sharedScheduleRepository;
        this.messageServices = messageService;
        this.imageRepository = imageRepository;
        this.commentRepository = commentRepository;
        this.messageService = NurigoApp.INSTANCE.initialize("NCSIK8AIEAUWTLBG", "YUQYOM9VHHRC0XMSRB7R6GKNTXZVPTKJ", "https://api.coolsms.co.kr");
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

        if (scheduleDto.isAlarm() && scheduleDto.getAlarmDateTime() == null) {
            throw new IllegalArgumentException("알람이 True인데 시간 설정이 안되었습니다.");
        }
        if (!scheduleDto.isAlarm() && scheduleDto.getAlarmDateTime()!=null) {
            throw new IllegalArgumentException("알람이 False인데 시간 설정이 되어있습니다.");
        }

        Schedule schedule = modelMapper.map(scheduleDto, Schedule.class);
        Member member = memberRepository.findById(scheduleDto.getMemberId()).orElse(null);
        schedule.setMember(member);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return modelMapper.map(savedSchedule, ScheduleDto.class);
    }

    @Transactional
    public ScheduleDto createSharedSchedule(ScheduleDto scheduleDto, List<String> sharedWithIds) {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        scheduleDto.setMemberId(myInfoBySecurity.getId());

        if (scheduleDto.isAlarm() && scheduleDto.getAlarmDateTime() == null) {
            throw new IllegalArgumentException("알람이 True인데 시간 설정이 안되었습니다.");
        }
        if (!scheduleDto.isAlarm() && scheduleDto.getAlarmDateTime()!=null) {
            throw new IllegalArgumentException("알람이 False인데 시간 설정이 되어있습니다.");
        }

        Schedule schedule = modelMapper.map(scheduleDto, Schedule.class);
        Member member = memberRepository.findById(scheduleDto.getMemberId()).orElse(null);
        schedule.setMember(member);

        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 공유 대상 멤버들에 대한 공유 스케줄 정보 저장
        List<Member> sharedWithMembers = memberRepository.findAllByEmailIn(sharedWithIds);
        List<SharedSchedule> sharedSchedules = sharedWithMembers.stream().map(sharedWith -> {
            SharedSchedule sharedSchedule = new SharedSchedule();
            sharedSchedule.setSchedule(savedSchedule);
            sharedSchedule.setMember(sharedWith);

            sharedScheduleRepository.save(sharedSchedule); // 공유 스케줄 저장 후 아이디값이 생성됨

            // 공유받는 멤버에게 메시지 전송
            String messageTitle = "새로운 공유 스케줄이 도착했습니다.";
            String messageContent = schedule.getTitle()+"";
            MessageDto messageDto = new MessageDto();
            messageDto.setSenderName(myInfoBySecurity.getNickname());
            messageDto.setReceiverName(sharedWith.getNickname());
            messageDto.setTitle(messageTitle);
            messageDto.setContent(messageContent);
            messageDto.setSharedScheduleId(sharedSchedule.getId()); // 공유 스케줄 아이디값 설정
            messageServices.write(messageDto); // 메시지 전송

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
        if(scheduleDto.getContent() != null && !scheduleDto.getContent().isEmpty()) {
            schedule.setContent(scheduleDto.getContent());
        }
        if(scheduleDto.getStartDateTime() != null) {
            schedule.setStartDateTime(scheduleDto.getStartDateTime());
        }
        if(scheduleDto.getEndDateTime() != null) {
            schedule.setEndDateTime(scheduleDto.getEndDateTime());
        }
        if(scheduleDto.getAlarmDateTime() != null){
            if(schedule.isAlarm()){
                schedule.setAlarmDateTime(scheduleDto.getAlarmDateTime());
            }
        }
        if(scheduleDto.isAlarm()!=schedule.isAlarm()){
            schedule.setAlarm(scheduleDto.isAlarm());
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
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule ID: " + id));
        List<SharedSchedule> sharedSchedules = sharedScheduleRepository.findByScheduleId(id);

        for (SharedSchedule sharedSchedule : sharedSchedules) {

            if (sharedSchedule.getMember().getId().equals(SecurityUtil.getCurrentMemberId())) {
                sharedScheduleRepository.delete(sharedSchedule);
            }
        }

        if(schedule.getMember().getId().equals(SecurityUtil.getCurrentMemberId())){

            for (SharedSchedule sharedSchedule : sharedSchedules) {
                 sharedScheduleRepository.delete(sharedSchedule);
                }
            List<Image> images = schedule.getImages(); // 예시: 스케줄과 이미지는 일대다 관계라고 가정
            for (Image image : images) {
                imageRepository.delete(image);
            }
            List<Comment> commentsRemove = commentRepository.findBySchedule(schedule);
            for (Comment comment : commentsRemove) {
                commentRepository.delete(comment);
            }
            scheduleRepository.delete(schedule);
        }
    }

    @Scheduled(cron = "0 * * * * *")// 1분마다 실행
    @Transactional
    public void SendOne() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime modifiedDateTime = currentDateTime.plusHours(9);
        log.info("Current DateTime: {}", currentDateTime);
        // alarmDateTime과 현재 시간 비교
        List<Schedule> schedules = scheduleRepository.findByAlarmDateTimeBefore(modifiedDateTime);

        for (Schedule schedule : schedules) {
            if (schedule.isAlarm()) {
                log.info("잘 들어옴 {}", schedule.getTitle());
                net.nurigo.sdk.message.model.Message message = new Message();
                // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
                Member member = schedule.getMember();
                message.setFrom("01033378486");
                message.setTo(member.getPhoneNumber());
                message.setText("금일은 " + schedule.getTitle() + " 일정이 있는 날입니다.");
                this.messageService.sendOne(new SingleMessageSendingRequest(message));
                schedule.setAlarm(false);
                scheduleRepository.save(schedule);
            }
        }
    }

}