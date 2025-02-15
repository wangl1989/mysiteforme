package com.mysiteforme.admin.redis;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
    
    private final  JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }
    
    // 生成JWT Token
    public String generateToken(UserDetails user) {
        Map<String, Object> claims = Maps.newHashMap();
        claims.put("username", user.getUsername());
        claims.put("authorities", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        return createToken(claims, user.getUsername(), jwtProperties.getAccessTokenExpiration());
    }
    
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)  // 替换 setClaims
                .setSubject(subject)  // 替换 setSubject
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 替换 setIssuedAt
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // 替换 setExpiration
                .signWith(getSigningKey())  // 更新的 signWith 方法，不需要显式指定算法
                .compact();
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    // 验证Token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);  // 替换 parseClaimsJws
            return true;
        } catch (Exception e) {
            log.error("Token validation failed: ", e);
            return false;
        }
    }
    
    // 从Token中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)  // 替换 parseClaimsJws
                .getBody();  // 替换 getBody
    }
}

