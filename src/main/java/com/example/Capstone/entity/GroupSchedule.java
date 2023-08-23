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

    private String backgroundColor;


    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private boolean alarm;

    private LocalDateTime alarmDateTime;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private MyGroup myGroup;

    @PrePersist
    @PreUpdate
    private void setEndDateTimeFromStart() {
        if (startDateTime != null && endDateTime == null) {
            // Set endDateTime as startDateTime + 1 hour (for example)
            endDateTime = startDateTime; // You can change the duration as needed
        }
    }

    @Builder
    public GroupSchedule(Long id, String title, String content, String backgroundColor, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean alarm, LocalDateTime alarmDateTime, List<Image> images, MyGroup myGroup) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.backgroundColor = backgroundColor;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.alarm = alarm;
        this.alarmDateTime = alarmDateTime;
        this.images = images;
        this.myGroup = myGroup;
    }
}
