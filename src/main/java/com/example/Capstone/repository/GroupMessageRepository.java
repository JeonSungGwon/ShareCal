package com.example.Capstone.repository;

import com.example.Capstone.entity.GroupMessage;
import com.example.Capstone.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByOwner(Member owner);
}

