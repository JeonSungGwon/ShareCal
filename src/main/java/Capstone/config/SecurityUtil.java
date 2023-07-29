package Capstone.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() { }

    // 현재 인증된 사용자의 회원 ID를 반환하는 메서드
    public static Long getCurrentMemberId() {
        // SecurityContextHolder에서 현재 인증 정보를 가져옴
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나 사용자명이 null인 경우 예외 처리
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        // 사용자명을 Long 타입으로 변환하여 회원 ID로 사용
        return Long.parseLong(authentication.getName());
    }
}
