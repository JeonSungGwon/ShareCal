package com.example.Capstone.api;

import com.example.Capstone.dto.MemberResponseDto;
import com.example.Capstone.dto.MessageDto;
import com.example.Capstone.entity.Member;
import com.example.Capstone.entity.SharedSchedule;
import com.example.Capstone.repository.MemberRepository;
import com.example.Capstone.repository.SharedScheduleRepository;
import com.example.Capstone.service.MemberService;
import com.example.Capstone.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Api(tags = "단일 메시지")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final MemberRepository memberRepository;

    private final SharedScheduleRepository sharedScheduleRepository;

    private final MemberService memberService;

    @PostMapping
    @ApiOperation(value = "메시지 전송")
    public ResponseEntity<MessageDto> write(@RequestBody MessageDto messageDto) {
        MessageDto savedMessage = messageService.write(messageDto);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/received")
    @ApiOperation(value = "내가 받은 메시지")
    public ResponseEntity<List<MessageDto>> receivedMessage() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member received = memberRepository.findByNickname(myInfoBySecurity.getNickname());
        List<MessageDto> messages = messageService.receivedMessage(received);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/accept/{id}")
    @ApiOperation(value = "단일 공유 메시지 승인")
    public ResponseEntity<String> approveSharedSchedule(@PathVariable Long id, @RequestBody Long sharedScheduleId) {
        SharedSchedule sharedSchedule = sharedScheduleRepository.findById(sharedScheduleId).orElse(null);
        sharedSchedule.setApproved(true);
        sharedScheduleRepository.save(sharedSchedule);
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        Member receiver = memberRepository.findByNickname(myInfoBySecurity.getNickname());
        messageService.deleteMessageByReceiver(id);
        return ResponseEntity.ok("Shared Schedule approved successfully");
    }

    @PostMapping("/reject/{id}")
    @ApiOperation(value = "단일 공유 메시지 거부")
    public ResponseEntity<String> disApproveSharedSchedule(@PathVariable Long id){
        messageService.deleteMessageByReceiver(id);
        return ResponseEntity.ok("Shared Schedule disapproved successfully");
    }



    @DeleteMapping("/received/{id}")
    @ApiOperation(value = "Delete a received message")
    public ResponseEntity<Object> deleteReceivedMessage(@PathVariable Long id, @RequestParam String memberNickname) {
        Member receiver = memberRepository.findByNickname(memberNickname);
        Object result = messageService.deleteMessageByReceiver(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sent")
    @ApiOperation(value = "Get sent messages")
    public ResponseEntity<List<MessageDto>> sentMessage(@RequestParam String memberNickname) {
        Member sender = memberRepository.findByNickname(memberNickname);
        List<MessageDto> messages = messageService.sentMessage(sender);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/sent/{id}")
    @ApiOperation(value = "Delete a sent message")
    public ResponseEntity<Object> deleteSentMessage(@PathVariable Long id, @RequestParam String memberNickname) {
        Member sender = memberRepository.findByNickname(memberNickname); //
        Object result = messageService.deleteMessageBySender(id, sender);
        return ResponseEntity.ok(result);
    }
}

