package com.example.Capstone.api;

import com.example.Capstone.config.SecurityUtil;
import com.example.Capstone.dto.GroupScheduleDto;
import com.example.Capstone.dto.ScheduleDto;
import com.example.Capstone.entity.Member;
import com.example.Capstone.repository.MemberRepository;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.model.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SMSController {
    final DefaultMessageService messageService;

    private final MemberRepository memberRepository;

    public SMSController(MemberRepository memberRepository) {
        // 반드시 계정 내 등록된 유효한 API 키, API Secret Key를 입력해주셔야 합니다!
        this.messageService = NurigoApp.INSTANCE.initialize("NCS1INCLK8BWN4SQ", "WQSKVMRU51E2HUOQRVAVQQE2ZXGDVLW5", "https://api.coolsms.co.kr");
        this.memberRepository = memberRepository;
    }

    @PostMapping("/send-one")
    public SingleMessageSentResponse sendOne(@RequestBody String scheduleTitle) {
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElse(null);
        message.setFrom(member.getPhoneNumber());
        message.setTo(member.getPhoneNumber());
        message.setText("금일은 "+scheduleTitle+" 일정이 있는 날입니다.");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return response;
    }

    @PostMapping("/group/send-one")
    public SingleMessageSentResponse groupSendOne(@RequestBody GroupScheduleDto groupScheduleDto) {
        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).orElse(null);
        message.setFrom(member.getPhoneNumber());
        message.setTo(member.getPhoneNumber());
        message.setText("금일은 "+groupScheduleDto.getTitle()+" 일정이 있는 날입니다.");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return response;
    }

}

