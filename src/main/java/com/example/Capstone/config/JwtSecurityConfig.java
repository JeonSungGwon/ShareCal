package com.example.Capstone.config;

import com.example.Capstone.jwt.JwtFilter;
import com.example.Capstone.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    // TokenProvider 객체
    private final TokenProvider tokenProvider;

    // HttpSecurity 구성
    @Override
    public void configure(HttpSecurity http) {
        // JwtFilter 객체 생성
        JwtFilter customFilter = new JwtFilter(tokenProvider);

        // UsernamePasswordAuthenticationFilter 앞에 JwtFilter 추가
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
