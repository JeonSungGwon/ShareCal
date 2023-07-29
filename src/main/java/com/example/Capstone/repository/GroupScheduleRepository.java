package com.example.Capstone.repository;

import com.example.Capstone.entity.GroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupScheduleRepository extends JpaRepository<GroupSchedule, Long> {
    List<GroupSchedule> findAllByMyGroup_Id(Long groupId);
    List<GroupSchedule> findAllByMyGroup_IdAndMyGroup_MemberGroups_Member_Id(Long groupId, Long memberId);

    List<GroupSchedule> findByAlarmDateTimeBefore(LocalDateTime alarmDateTime);
}