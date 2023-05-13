package com.example.Capstone.api;

import com.example.Capstone.dto.GroupScheduleDto;
import com.example.Capstone.service.GroupScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/schedules")
public class GroupScheduleController {

    private final GroupScheduleService groupScheduleService;

    public GroupScheduleController(GroupScheduleService groupScheduleService) {
        this.groupScheduleService = groupScheduleService;
    }

    @PostMapping
    public ResponseEntity<Long> createGroupSchedule(@PathVariable Long groupId, @RequestBody GroupScheduleDto groupScheduleDto) {
        groupScheduleDto.setGroupId(groupId);
        Long id = groupScheduleService.createGroupSchedule(groupScheduleDto);
        return ResponseEntity.ok(id);
    }

    @PatchMapping("/{groupScheduleId}")
    public GroupScheduleDto updateGroupSchedule(@PathVariable Long groupScheduleId, @PathVariable Long groupId, @RequestBody GroupScheduleDto groupScheduleDto) {
        return groupScheduleService.updateGroupSchedule(groupScheduleId, groupId, groupScheduleDto);
    }

    @DeleteMapping("/{groupScheduleId}")
    public ResponseEntity<Void> deleteGroupSchedule(@PathVariable Long groupScheduleId) {
        groupScheduleService.deleteGroupSchedule(groupScheduleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<GroupScheduleDto>> getGroupSchedules(@PathVariable Long groupId) {
        List<GroupScheduleDto> groupScheduleDtos = groupScheduleService.getGroupSchedules(groupId);
        return ResponseEntity.ok(groupScheduleDtos);
    }
}
