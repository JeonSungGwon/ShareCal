package com.example.Capstone.repository;

import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.MemberGroup;
import com.example.Capstone.entity.MyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long> {
    boolean existsByGroupAndMember(MyGroup group, Member member);
}

