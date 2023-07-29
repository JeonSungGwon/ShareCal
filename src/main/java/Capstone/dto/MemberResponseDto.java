package Capstone.dto;

import com.example.Capstone.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private String phoneNumber;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .id(member.getId())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }
}