package com.example.Capstone.service;

import com.example.Capstone.dto.ScheduleParticipantDto;
import com.example.Capstone.entity.ScheduleParticipant;
import com.example.Capstone.repository.ScheduleParticipantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ScheduleParticipantService {

    private final ScheduleParticipantRepository scheduleParticipantRepository;
    private final ModelMapper modelMapper;

    public ScheduleParticipantService(ScheduleParticipantRepository scheduleParticipantRepository, ModelMapper modelMapper) {
        this.scheduleParticipantRepository = scheduleParticipantRepository;
        this.modelMapper = modelMapper;
    }

    public List<ScheduleParticipantDto> getParticipantsByScheduleId(Long scheduleId) {
        List<ScheduleParticipant> participants = scheduleParticipantRepository.findBySchedulesId(scheduleId);
        return participants.stream()
                .map(participant -> modelMapper.map(participant, ScheduleParticipantDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleParticipantDto createParticipant(ScheduleParticipantDto scheduleParticipantDto) {
        ScheduleParticipant participant = modelMapper.map(scheduleParticipantDto, ScheduleParticipant.class);
        ScheduleParticipant savedParticipant = scheduleParticipantRepository.save(participant);
        return modelMapper.map(savedParticipant, ScheduleParticipantDto.class);
    }

    @Transactional
    public ScheduleParticipantDto updateParticipant(Long id, ScheduleParticipantDto scheduleParticipantDto) {
        ScheduleParticipant participant = scheduleParticipantRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such participant"));
        participant.setName(scheduleParticipantDto.getUsername());
        ScheduleParticipant updatedParticipant = scheduleParticipantRepository.save(participant);
        return modelMapper.map(updatedParticipant, ScheduleParticipantDto.class);
    }

    @Transactional
    public void deleteParticipant(Long id) {
        scheduleParticipantRepository.deleteById(id);
    }
}