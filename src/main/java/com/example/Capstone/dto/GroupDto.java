package com.example.Capstone.dto;

import com.example.Capstone.entity.Member;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class GroupDto {

    private Long id;
    private String name;

    private List<Long> memberIds;
    private List<MemberDto> members;

    @Builder
    public GroupDto(Long id, String name, List<Member> members) {
        this.id = id;
        this.name = name;
        this.memberIds = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        this.members = members.stream()
                .map(member -> new MemberDto(member.getId(), member.getEmail(), member.getNickname(), member.getAuthority()))
                .collect(Collectors.toList());
    }
}
