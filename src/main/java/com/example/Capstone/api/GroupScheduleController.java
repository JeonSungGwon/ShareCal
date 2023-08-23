package com.example.Capstone.api;

import com.example.Capstone.dto.GroupScheduleDto;
import com.example.Capstone.service.GroupScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/schedules")
@Api(tags = "그룹일정")
public class GroupScheduleController {

    private final GroupScheduleService groupScheduleService;

    public GroupScheduleController(GroupScheduleService groupScheduleService) {
        this.groupScheduleService = groupScheduleService;
    }

    @PostMapping
    @Operation(summary = "그룹 스케줄 생성")
    public ResponseEntity<Long> createGroupSchedule(@PathVariable Long groupId, @RequestBody GroupScheduleDto groupScheduleDto) {
        groupScheduleDto.setGroupId(groupId);
        Long id = groupScheduleService.createGroupSchedule(groupScheduleDto);
        return ResponseEntity.ok(id);
    }

    @PatchMapping("/{groupScheduleId}")
    @Operation(summary = "그룹 스케줄 수정")
    public GroupScheduleDto updateGroupSchedule(@PathVariable Long groupScheduleId, @PathVariable Long groupId, @RequestBody GroupScheduleDto groupScheduleDto, @RequestParam(required = false) MultipartFile image) {
        return groupScheduleService.updateGroupSchedule(groupScheduleId, groupId, groupScheduleDto, image);
    }

    @DeleteMapping("/{groupScheduleId}")
    @Operation(summary = "그룹 스케줄 삭제")
    public ResponseEntity<Void> deleteGroupSchedule(@PathVariable Long groupScheduleId) {
        groupScheduleService.deleteGroupSchedule(groupScheduleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "해당하는 그룹 스케줄 모두 가져오그")
    public ResponseEntity<List<GroupScheduleDto>> getGroupSchedules(@PathVariable Long groupId) {
        List<GroupScheduleDto> groupScheduleDtos = groupScheduleService.getGroupSchedules(groupId);
        return ResponseEntity.ok(groupScheduleDtos);
    }
}
