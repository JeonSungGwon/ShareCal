package com.example.Capstone.repository;

import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findAllByReceiver(Member member);

    List<Message> findAllBySender(Member member);


}
