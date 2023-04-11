package com.example.Capstone.service;

import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.entity.Schedule;
import com.example.Capstone.repository.ScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ModelMapper modelMapper;

    public ScheduleService(ScheduleRepository scheduleRepository, ModelMapper modelMapper) {
        this.scheduleRepository = scheduleRepository;
        this.modelMapper = modelMapper;
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
        Schedule schedule = modelMapper.map(scheduleDto, Schedule.class);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return modelMapper.map(savedSchedule, ScheduleDto.class);
    }

    @Transactional
    public ScheduleDto updateSchedule(Long id, ScheduleDto scheduleDto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such schedule"));
        schedule.setTitle(scheduleDto.getTitle());
        schedule.setStartDateTime(scheduleDto.getStartDateTime());
        schedule.setEndDateTime(scheduleDto.getEndDateTime());
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return modelMapper.map(updatedSchedule, ScheduleDto.class);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}