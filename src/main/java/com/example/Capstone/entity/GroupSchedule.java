package com.example.Capstone.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GroupSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    // 다대다 매핑을 위한 group 엔티티와의 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private MyGroup myGroup;

    @Builder
    public GroupSchedule(Long id, String title, LocalDateTime startDateTime, LocalDateTime endDateTime, MyGroup myGroup) {
        this.id = id;
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.myGroup = myGroup;
    }
}
