package com.example.graduation_project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 自动生成安全密钥
    private static final long EXPIRATION_TIME = 86400000; // 24小时（单位：毫秒）

    // 生成 JWT 令牌
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 JWT
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 验证 JWT 是否有效
    public boolean validateToken(String token) {
        return getClaims(token).getExpiration().after(new Date());
    }

    // 获取 JWT 中的 Claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

