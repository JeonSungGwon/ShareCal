package Capstone.dto;

import com.example.Capstone.entity.Authority;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberDto {
    private Long id;
    private String email;
    private String nickname;
    private Authority authority;
    private String phoneNumber;

    @Builder
    public MemberDto(Long id, String email, String nickname, Authority authority, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.authority = authority;
        this.phoneNumber = phoneNumber;
    }
}
