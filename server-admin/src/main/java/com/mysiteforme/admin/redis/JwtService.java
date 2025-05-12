package com.mysiteforme.admin.redis;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mysiteforme.admin.config.JwtProperties;
import com.mysiteforme.admin.entity.DTO.TokenValidationResult;
import com.mysiteforme.admin.security.MyUserDetails;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.TokenErrorType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
    
    private final  JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }
    
    /**
     * 为给定的用户详情生成JWT Token。
     *
     * @param user 用户详情，包含用户名和权限信息。
     * @return 生成的JWT Token字符串。
     */
    public String generateToken(MyUserDetails user) {
        // 创建一个空的claims（声明）集合，用于存储JWT中的用户信息。
        Map<String, Object> claims = Maps.newHashMap();

        // 将用户的用户名添加到claims中，作为JWT的一部分。
        claims.put("username", user.getUsername());
        claims.put("deviceId", user.getDeviceId());
        // 将用户的权限信息添加到claims中，作为JWT的一部分。
        // 这里通过流操作将GrantedAuthority对象转换为权限字符串列表。
        claims.put("authorities", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        // 调用createToken方法，根据claims、用户名和访问令牌的过期时间生成JWT Token。
        // jwtProperties.getAccessTokenExpiration() 提供访问令牌的过期时间。
        return createToken(claims, user.getUsername(), jwtProperties.getAccessTokenExpiration());
    }

    /**
     * 生成刷新令牌（refresh token）
     * <p>
     * 该方法根据用户详细信息生成一个刷新令牌，其中包含了令牌的类型、用户名，
     * 以及令牌的发行时间和过期时间此外，还使用了HS256算法对令牌进行签名以确保其安全性
     *
     * @param userDetails 用户详细信息，用于获取用户名
     * @return 返回生成的刷新令牌字符串
     */
    public String generateRefreshToken(MyUserDetails userDetails) {
        // 初始化claims，用于存放payload中的自定义信息
        Map<String, Object> claims = Maps.newHashMap();
        // 设置claims中的类型为"refresh"，表示这是一个刷新令牌
        claims.put("deviceId", userDetails.getDeviceId());
        claims.put("type", "refresh");
        // 设置claims中的用户名，用于标识用户
        claims.put("username", userDetails.getUsername());
        // 使用Jwts.builder构建JWT
        return Jwts.builder()
                // 设置令牌中的claims
                .setClaims(claims)
                // 设置令牌的主题，即用户名
                .setSubject(userDetails.getUsername())
                // 设置令牌的发行时间为当前时间
                .setIssuedAt(new Date())
                // 设置令牌的过期时间为当前时间加上刷新令牌的过期时间
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpiration()))
                // 使用HS256算法对令牌进行签名
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                // 将构建好的JWT压缩为字符串形式并返回
                .compact();
    }

    public TokenValidationResult validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if(claims.get("type").equals("refresh")&& !isTokenExpired(token)){
                return new TokenValidationResult(true);
            }
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_REFRESH_TOKEN_INVALID, TokenErrorType.INVALID);
        } catch (ExpiredJwtException e) {
            log.warn("令牌已过期: {}", e.getMessage());
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_EXPIRED, TokenErrorType.EXPIRED);
        } catch (MalformedJwtException e) {
            log.error("无效的令牌格式: {}", e.getMessage());
            // 令牌不是有效的JWT格式（应为三段式结构，以点分隔）
            // Base64解码失败（可能包含非法字符）
            // 令牌在传输过程中被截断或损坏
            // 令牌的JSON结构无法正确解析
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_MALFORMED, TokenErrorType.MALFORMED);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的令牌类型: {}", e.getMessage());
            // 令牌使用了系统不支持的加密或签名算法
            // JWT包含了库不支持的声明或特性
            // 令牌版本与当前系统实现不兼容
            // 可能是在系统升级后未正确处理历史令牌
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_UNSUPPORTED, TokenErrorType.UNSUPPORTED);
        } catch (SignatureException e) {
            log.error("令牌签名验证失败，可能被篡改: {}", e.getMessage());
            // 令牌被篡改（头部或载荷部分被修改）
            // 使用了错误的密钥验证令牌
            // 多服务器环境中密钥不同步
            // 签名算法不匹配（创建和验证使用了不同的算法）
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_SIGNATURE_FAILED, TokenErrorType.SIGNATURE_FAILED);
        } catch (IllegalArgumentException e) {
            log.error("令牌参数无效: {}", e.getMessage());
            // 传入的token为null或空字符串
            // token参数结构明显不符合JWT要求（如长度过短）
            // 签名密钥配置有问题（如为null）
            // 前端未正确传递或存储令牌
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_INVALID_ARGUMENT, TokenErrorType.INVALID_ARGUMENT);
        }
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
    public TokenValidationResult validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);  // 替换 parseClaimsJws
            return new TokenValidationResult(true);
        } catch (ExpiredJwtException e) {
            log.warn("令牌已过期: {}", e.getMessage());
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_EXPIRED, TokenErrorType.EXPIRED);
        } catch (MalformedJwtException e) {
            log.error("无效的令牌格式: {}", e.getMessage());
            // 令牌不是有效的JWT格式（应为三段式结构，以点分隔）
            // Base64解码失败（可能包含非法字符）
            // 令牌在传输过程中被截断或损坏
            // 令牌的JSON结构无法正确解析
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_MALFORMED, TokenErrorType.MALFORMED);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的令牌类型: {}", e.getMessage());
            // 令牌使用了系统不支持的加密或签名算法
            // JWT包含了库不支持的声明或特性
            // 令牌版本与当前系统实现不兼容
            // 可能是在系统升级后未正确处理历史令牌
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_UNSUPPORTED, TokenErrorType.UNSUPPORTED);
        } catch (SignatureException e) {
            log.error("令牌签名验证失败，可能被篡改: {}", e.getMessage());
            // 令牌被篡改（头部或载荷部分被修改）
            // 使用了错误的密钥验证令牌
            // 多服务器环境中密钥不同步
            // 签名算法不匹配（创建和验证使用了不同的算法）
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_SIGNATURE_FAILED, TokenErrorType.SIGNATURE_FAILED);
        } catch (IllegalArgumentException e) {
            log.error("令牌参数无效: {}", e.getMessage());
            // 传入的token为null或空字符串
            // token参数结构明显不符合JWT要求（如长度过短）
            // 签名密钥配置有问题（如为null）
            // 前端未正确传递或存储令牌
            return new TokenValidationResult(false, MessageConstants.JwtToken.JWT_TOKEN_INVALID_ARGUMENT, TokenErrorType.INVALID_ARGUMENT);
        }
    }

    public boolean validateDeviceId(String token, String deviceId) {
        return extractDeviceId(token).equals(deviceId);
    }
    
    // 从Token中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从Token中提取设备ID
    public String extractDeviceId(String token) {
        return extractAllClaims(token).get("deviceId", String.class);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("token超时，验证出现异常", e);
            return true; // 如果解析失败，认为token过期
        }
    }

    // 配套的extractExpiration方法
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)  // 替换 parseClaimsJws
                .getBody();  // 替换 getBody
    }
}

