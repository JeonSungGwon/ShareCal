package com.example.Capstone.repository;

import com.example.Capstone.entity.GroupSchedule;
import com.example.Capstone.entity.Schedule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByAlarmDateTimeBefore(LocalDateTime alarmDateTime);

}
