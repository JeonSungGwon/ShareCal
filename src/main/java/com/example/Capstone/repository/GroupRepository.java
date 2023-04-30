package com.example.Capstone.repository;


import com.example.Capstone.entity.MyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<MyGroup, Long> {
    Optional<MyGroup> findById(Long id);
}
