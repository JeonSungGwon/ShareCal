package com.example.Capstone.jwt;

import com.example.Capstone.SimpleGrantedAuthority;
import com.example.Capstone.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Getter
@Slf4j
public class TokenProvider {

    // 토큰에서 권한 정보를 저장하는 클레임(Claim)의 키
    private static final String AUTHORITIES_KEY = "auth";
    // 생성되는 토큰의 타입을 나타내는 문자열
    private static final String BEARER_TYPE = "bearer";
    // 생성된 액세스 토큰의 유효 기간 (30분)
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 600 * 30;
    // JWT 서명을 생성하기 위한 키
    private final Key key;

    // 주의점: 여기서 @Value는 `springframework.beans.factory.annotation.Value` 소속이다! lombok의 @Value와 착각하지 말 것!
    // secretKey 값을 통해 JWT 서명을 생성하기 위한 키를 초기화
    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        log.info(secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public TokenDto generateTokenDto(Authentication authentication) {
        // 사용자의 권한 정보를 쉼표로 구분하여 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 현재 시간
        long now = (new Date()).getTime();

        // 토큰 만료 시간 설정 (현재 시간으로부터 30분 뒤)
        Date tokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        // JWT 토큰 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(tokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        // TokenDto 객체 생성 및 반환
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .tokenExpiresIn(tokenExpiresIn.getTime())
                .build();
    }

    // 토큰을 사용하여 인증 객체 생성
    public Authentication getAuthentication(String accessToken) {
        // 토큰에서 클레임(Claim) 파싱
        Claims claims = parseClaims(accessToken);

        // 권한 정보가 없는 경우 예외 처리
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 토큰의 권한 정보를 가져와서 GrantedAuthority 객체로 변환하여 Collection 생성
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체 생성
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // UsernamePasswordAuthenticationToken을 사용하여 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 토큰의 서명 검증
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 토큰의 클레임 파싱
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
