package com.example.Capstone.repository;

import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByMembers(Member member);
    Todo findByMember(Member member);
}
