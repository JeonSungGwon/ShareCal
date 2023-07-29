package com.example.Capstone.service;

import com.example.Capstone.config.SecurityUtil;
import com.example.Capstone.dto.CommentDTO;
import com.example.Capstone.dto.GroupScheduleDto;
import com.example.Capstone.entity.Comment;
import com.example.Capstone.entity.GroupSchedule;
import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.Schedule;
import com.example.Capstone.repository.CommentRepository;
import com.example.Capstone.repository.GroupScheduleRepository;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.repository.ScheduleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupScheduleRepository groupScheduleRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository, MemberRepository memberRepository, ScheduleRepository scheduleRepository,
                          ModelMapper modelMapper,GroupScheduleRepository groupScheduleRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.scheduleRepository = scheduleRepository;
        this.modelMapper = modelMapper;
        this.groupScheduleRepository = groupScheduleRepository;
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());

        if(commentDTO.getScheduleId()!=null) {
                Schedule schedule = scheduleRepository.findById(commentDTO.getScheduleId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID"));

            comment.setSchedule(schedule);
        }
        if(commentDTO.getGroupScheduleId()!=null) {
            GroupSchedule groupSchedule = groupScheduleRepository.findById(commentDTO.getGroupScheduleId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID"));

            comment.setGroupSchedule(groupSchedule);
        }

        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElse(null);

        comment.setMember(member);

        Comment savedComment = commentRepository.save(comment);

        ModelMapper modelMapper = new ModelMapper();

        CommentDTO savedCommentDTO = modelMapper.map(savedComment, CommentDTO.class);

        return savedCommentDTO;
    }
    public CommentDTO getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Comment ID"));

        return modelMapper.map(comment, CommentDTO.class);
    }

    public List<CommentDTO> getCommentsBySchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID"));

        List<Comment> comments = commentRepository.findBySchedule(schedule);

        return comments.stream()
                .map(comment -> {
                    CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
                    commentDTO.setMemberId(comment.getMember().getId()); // member의 memberId를 commentDTO에 설정
                    commentDTO.setMemberNickname(comment.getMember().getNickname());
                    return commentDTO;
                })
                .collect(Collectors.toList());
    }
    public List<CommentDTO> getCommentsByGroupSchedule(Long groupScheduleId) {
        GroupSchedule groupSchedule = groupScheduleRepository.findById(groupScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Schedule ID"));

        List<Comment> comments = commentRepository.findByGroupSchedule(groupSchedule);

        return comments.stream()
                .map(comment -> {
                    CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
                    commentDTO.setMemberId(comment.getMember().getId()); // member의 memberId를 commentDTO에 설정
                    commentDTO.setMemberNickname(comment.getMember().getNickname());
                    return commentDTO;
                })
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Comment ID"));

        // 현재 로그인된 사용자의 ID를 가져옵니다.
        Long currentMemberId = SecurityUtil.getCurrentMemberId();

        // 현재 로그인된 사용자의 ID와 댓글 작성자의 ID를 비교하여 일치할 경우에만 삭제합니다.
        if (comment.getMember().getId().equals(currentMemberId)) {
            commentRepository.delete(comment);
        } else {
            throw new IllegalArgumentException("You don't have permission to delete this comment.");
        }
    }

    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Comment ID"));

        // 현재 로그인된 사용자의 ID를 가져옵니다.
        Long currentMemberId = SecurityUtil.getCurrentMemberId();

        // 현재 로그인된 사용자의 ID와 댓글 작성자의 ID를 비교하여 일치할 경우에만 수정합니다.
        if (comment.getMember().getId().equals(currentMemberId)) {
            comment.setText(commentDTO.getText());

            Comment updatedComment = commentRepository.save(comment);

            return modelMapper.map(updatedComment, CommentDTO.class);
        } else {
            throw new IllegalArgumentException("You don't have permission to update this comment.");
        }
    }
}
