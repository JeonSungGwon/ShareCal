package com.example.Capstone.api;

import com.example.Capstone.dto.ImageDto;
import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.entity.Image;
import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.Schedule;
import com.example.Capstone.entity.SharedSchedule;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.repository.SharedScheduleRepository;
import com.example.Capstone.service.MemberService;
import com.example.Capstone.service.ScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final MemberService memberService;

    private final MemberRepository memberRepository;

    private final ModelMapper modelMapper;

    private final SharedScheduleRepository sharedScheduleRepository;



    public ScheduleController(ScheduleService scheduleService, MemberService memberService, MemberRepository memberRepository,ModelMapper modelMapper,SharedScheduleRepository sharedScheduleRepository) {

        this.scheduleService = scheduleService;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        this.sharedScheduleRepository=sharedScheduleRepository;
    }

    @GetMapping("/user")
    public List<ScheduleDto> getMySchedules() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member member = memberRepository.findById(myInfoBySecurity.getId()).orElseThrow(() -> new EntityNotFoundException("Member not found"));
        List<Schedule> schedules = member.getSchedules();
        if (!sharedScheduleRepository.findByMemberId(member.getId()).isEmpty()) {
            List<SharedSchedule> sharedSchedules = sharedScheduleRepository.findByMemberId(member.getId());
            for (SharedSchedule sharedSchedule : sharedSchedules) {
                if (sharedSchedule.isApproved()) {
                    schedules.add(sharedSchedule.getSchedule());
                }
            }
        }
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        for (Schedule schedule : schedules) {
            ScheduleDto scheduleDto = modelMapper.map(schedule, ScheduleDto.class);

            // 이미지 처리
            List<Image> images = schedule.getImages();
            if (images != null && !images.isEmpty()) {
                List<ImageDto> imageDtos = new ArrayList<>();
                for (Image image : images) {
                    ImageDto imageDto = new ImageDto();
                    byte[] imageBytes = image.getImageData();
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    imageDto.setImageData(base64Image);
                    imageDtos.add(imageDto);
                }
                scheduleDto.setImages(imageDtos);
            }

            scheduleDtos.add(scheduleDto);
        }
        return scheduleDtos;
    }

    @GetMapping("")
    public List<ScheduleDto> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ScheduleDto getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id);
    }

    @PostMapping("")
    public ScheduleDto createSchedule(@RequestBody ScheduleDto scheduleDto) {
        return scheduleService.createSchedule(scheduleDto);
    }

    @PostMapping("/shared")
    public ResponseEntity<ScheduleDto> createSharedSchedule(@RequestBody ScheduleDto scheduleDto,
                                                            @RequestParam List<Long> sharedWithIds) {
        ScheduleDto savedSchedule = scheduleService.createSharedSchedule(scheduleDto, sharedWithIds);
        return ResponseEntity.ok(savedSchedule);
    }

    @PatchMapping("/{id}")
    public ScheduleDto updateSchedule(@PathVariable Long id, @RequestBody ScheduleDto scheduleDto,@RequestParam(required = false) MultipartFile image) {
        return scheduleService.updateSchedule(id, scheduleDto, image);
    }

    @PatchMapping("/image/{id}")
    public ScheduleDto updateImage(@PathVariable Long id,@RequestParam(required = false) MultipartFile image) {
        return scheduleService.updateImage(id, image);
    }


    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
    }

}
