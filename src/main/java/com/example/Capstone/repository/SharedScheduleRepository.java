package com.example.Capstone.repository;

import com.example.Capstone.entity.SharedSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedScheduleRepository extends JpaRepository<SharedSchedule, Long> {
    SharedSchedule findByScheduleId(Long scheduleId);

    List<SharedSchedule> findByMemberId(Long memberId);
}
