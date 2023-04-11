package com.example.Capstone.api;

import com.example.Capstone.dto.ScheduleParticipantDto;
import com.example.Capstone.service.ScheduleParticipantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-participants")
public class ScheduleParticipantController {

    private final ScheduleParticipantService scheduleParticipantService;

    public ScheduleParticipantController(ScheduleParticipantService scheduleParticipantService) {
        this.scheduleParticipantService = scheduleParticipantService;
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<List<ScheduleParticipantDto>> getParticipantsByScheduleId(@PathVariable Long scheduleId) {
        List<ScheduleParticipantDto> participants = scheduleParticipantService.getParticipantsByScheduleId(scheduleId);
        return ResponseEntity.ok(participants);
    }

    @PostMapping
    public ResponseEntity<ScheduleParticipantDto> createParticipant(@RequestBody ScheduleParticipantDto scheduleParticipantDto) {
        ScheduleParticipantDto createdParticipant = scheduleParticipantService.createParticipant(scheduleParticipantDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdParticipant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleParticipantDto> updateParticipant(@PathVariable Long id, @RequestBody ScheduleParticipantDto scheduleParticipantDto) {
        ScheduleParticipantDto updatedParticipant = scheduleParticipantService.updateParticipant(id, scheduleParticipantDto);
        return ResponseEntity.ok(updatedParticipant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        scheduleParticipantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }
}
