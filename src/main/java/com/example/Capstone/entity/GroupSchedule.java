package com.example.Capstone.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String content;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private boolean alarm;

    private LocalDateTime alarmDateTime;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    // 다대다 매핑을 위한 group 엔티티와의 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private MyGroup myGroup;

    @Builder
    public GroupSchedule(Long id, String title, String content, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean alarm, LocalDateTime alarmDateTime, List<Image> images, MyGroup myGroup) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.alarm = alarm;
        this.alarmDateTime = alarmDateTime;
        this.images = images;
        this.myGroup = myGroup;
    }
}
