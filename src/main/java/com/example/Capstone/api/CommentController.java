package com.example.Capstone.api;

import com.example.Capstone.dto.CommentDTO;
import com.example.Capstone.entity.Comment;
import com.example.Capstone.service.CommentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        return new ResponseEntity<>(commentService.createComment(commentDTO), HttpStatus.CREATED);
    }

    @ApiOperation(value = "단일 댓글 조회")
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long commentId) {
        CommentDTO comment = commentService.getComment(commentId);
        return ResponseEntity.ok(comment);
    }
    @ApiOperation(value = "스케줄에 있는 댓글 모두 조회")
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<CommentDTO>> getCommentsBySchedule(@PathVariable Long scheduleId) {
        List<CommentDTO> comments = commentService.getCommentsBySchedule(scheduleId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/groupSchedule/{groupScheduleId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByGroupSchedule(@PathVariable Long groupScheduleId) {
        List<CommentDTO> comments = commentService.getCommentsByGroupSchedule(groupScheduleId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO) {
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
    // Other endpoints for comment management (update, delete, etc.)
}