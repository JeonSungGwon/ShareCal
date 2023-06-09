package com.example.Capstone.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sharedCode;

    // 다대다 매핑을 위한 members 리스트
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_member",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private List<Member> members = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;


    // Schedule 엔티티와 다대다 매핑을 위한 schedules 리스트
    @OneToMany(mappedBy = "myGroup")
    private List<GroupSchedule> groupSchedules = new ArrayList<>();

    @Builder
    public MyGroup(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}