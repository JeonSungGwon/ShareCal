package com.example.Capstone.service;

import com.example.Capstone.dto.GroupDto;
import com.example.Capstone.entity.MyGroup;
import com.example.Capstone.entity.Member;
import com.example.Capstone.repository.GroupRepository;
import com.example.Capstone.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GroupService {

    private final ModelMapper modelMapper;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    private final MemberService memberService;

    public GroupService(GroupRepository groupRepository, MemberRepository memberRepository, ModelMapper modelMapper, MemberService memberService) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.modelMapper=modelMapper;
        this.memberService=memberService;
    }

    @Transactional
    public Long createGroup(GroupDto groupDto) {
        MyGroup myGroup = MyGroup.builder()
                .name(groupDto.getName())
                .build();
        return groupRepository.save(myGroup).getId();
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        MyGroup myGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupId));
        groupRepository.delete(myGroup);
    }

    @Transactional
    public void addMemberToGroup(Long groupId, Long memberId) {
        MyGroup myGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));
        myGroup.getMembers().add(member);
        groupRepository.save(myGroup);
    }

    @Transactional
    public void removeMemberFromGroup(Long groupId, Long memberId) {
        MyGroup myGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));
        myGroup.getMembers().remove(member);
        groupRepository.save(myGroup);
    }

    public GroupDto getGroup(Long groupId) {
        MyGroup myGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID: " + groupId));
        System.out.println(myGroup.getName());
        return modelMapper.map(myGroup, GroupDto.class);
    }

}
