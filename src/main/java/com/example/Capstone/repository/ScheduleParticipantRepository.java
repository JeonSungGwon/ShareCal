package com.example.Capstone.repository;

import com.example.Capstone.entity.ScheduleParticipant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, Long> {

    List<ScheduleParticipant> findBySchedulesId(Long scheduleId);

    //Optional<ScheduleParticipant> findByScheduleIdAndUserId(Long scheduleId, Long userId);
}
