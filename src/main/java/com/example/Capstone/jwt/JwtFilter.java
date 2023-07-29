package com.example.Capstone.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // Authorization 헤더의 이름
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // Bearer 토큰 접두사
    public static final String BEARER_PREFIX = "Bearer ";
    // TokenProvider 객체
    private final TokenProvider tokenProvider;

    // 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // Bearer 접두사 제거 후 토큰 반환
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 토큰 추출
        String jwt = resolveToken(request);

        // 토큰이 유효하고 인증 정보를 가져올 수 있는 경우
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            // 인증 정보를 SecurityContext에 설정
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}
