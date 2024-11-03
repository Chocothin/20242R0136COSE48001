package com.example.choco_planner.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "your_secret_key_your_secret_key_your_secret_key"; // 256비트(32자 이상) 키 필요
    private final long EXPIRATION_TIME = 86400000; // 24시간 (밀리초 단위)

    // 비밀키 생성
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // JWT 생성
    public String generateToken(Long id, String username, String name) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("name", name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT에서 사용자 이름 추출
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // JWT에서 사용자 ID 추출
    public Long extractUserId(String token) {
        return getClaims(token).get("id", Long.class);
    }

    // JWT에서 사용자 이름 추출
    public String extractName(String token) {
        return getClaims(token).get("name", String.class);
    }

    // JWT 유효성 검증
    public boolean isTokenValid(String token, String username) {
        return (extractUsername(token).equals(username)) && !isTokenExpired(token);
    }

    // 특정 클레임 추출
    public static String extractClaim(String token, String claim) {
        return getClaims(token).get(claim, String.class);
    }

    // JWT 만료 여부 확인
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // 토큰에서 Claims 추출
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
