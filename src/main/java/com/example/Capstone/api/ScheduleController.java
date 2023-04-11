package com.example.Capstone.api;

import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
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

    @PutMapping("/{id}")
    public ScheduleDto updateSchedule(@PathVariable Long id, @RequestBody ScheduleDto scheduleDto) {
        return scheduleService.updateSchedule(id, scheduleDto);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
    }
}
