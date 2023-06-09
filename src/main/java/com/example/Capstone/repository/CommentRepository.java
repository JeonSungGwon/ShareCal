package com.example.Capstone.repository;

import com.example.Capstone.entity.Comment;
import com.example.Capstone.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySchedule(Schedule schedule);
    // 추가적인 커스텀 쿼리 또는 메서드가 필요한 경우 작성할 수 있습니다.
}
