package com.mysiteforme.admin.redis;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.mysiteforme.admin.util.Constants;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.mysiteforme.admin.config.JwtProperties;
import com.mysiteforme.admin.entity.VO.DeviceTokenInfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenStorageService {

    private final RedisUtils redisUtils;

    private final JwtProperties jwtProperties;


    /**
     *  存储刷新令牌到Redis缓存
     * <p>
     *  该方法将刷新令牌与用户绑定存储，使用Redis键值对结构保存，并设置基于配置的过期时间。
     *  键构造规则：RedisConstants.ACCESS_TOKEN_PREFIX + username 组成复合键
     *  值存储内容：JWT刷新令牌字符串
     *
     * @param username 用户标识，用于构造Redis存储键，通常为用户唯一ID或登录名
     * @param refreshToken JWT刷新令牌字符串，用于后续获取新访问令牌的凭证
     * @param deviceId 用户设备ID
     */
    public void storeRefreshToken(String username, String refreshToken, String deviceId) {
        // 1. 存储刷新令牌
        String key = RedisConstants.REFRESH_TOKEN_PREFIX + username + ":" + deviceId;
        redisUtils.set(key, refreshToken, jwtProperties.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);
        
        // 注：用户-设备绑定关系已在 storeAccessToken 中处理
    }

    /**
     * 存储用户的访问令牌到Redis中。
     * <p>
     * 该方法将用户的访问令牌与用户名关联，并将其存储在Redis中。存储的键由Redis常量前缀和用户名组成，
     * 值的过期时间由JWT配置中的访问令牌过期时间决定。
     *
     * @param username 用户名，用于生成Redis存储的键。
     * @param accessToken 用户的访问令牌，将被存储在Redis中。
     * @param deviceId 用户设备ID
     */
    public void storeAccessToken(String username, String accessToken, String deviceId) {
        // 1. 存储访问令牌
        String key = RedisConstants.ACCESS_TOKEN_PREFIX + username + ":" + deviceId;
        redisUtils.set(
                key,
                accessToken,
                jwtProperties.getAccessTokenExpiration(),
                TimeUnit.MILLISECONDS
        );
        // 2. 维护用户-设备映射关系（使用Hash结构存储当前有效的刷新令牌）
        String userDeviceKey = RedisConstants.USER_DEVICE_PREFIX + username;
        redisUtils.sSet(userDeviceKey, Constants.USER_DEVICE_KEY_TIME,TimeUnit.DAYS, deviceId);
    }

    /**
     * 验证访问令牌的有效性。
     * <p>
     * 该函数通过从Redis中获取存储的访问令牌，并与传入的令牌进行比较，来判断令牌是否有效。
     *
     * @param username 用户名，用于构建Redis中的键。
     * @param token 需要验证的访问令牌。
     * @param deviceId 设备ID，用于构建Redis中的键。
     * @return 如果传入的令牌与Redis中存储的令牌一致，则返回true，否则返回false。
     */
    public boolean validateAccessToken(String username, String token, String deviceId) {
        // 构建Redis中的键，格式为：ACCESS_TOKEN_PREFIX + username + ":" + deviceId
        String key = RedisConstants.ACCESS_TOKEN_PREFIX + username + ":" + deviceId;

        // 从Redis中获取存储的访问令牌
        String storedToken = redisUtils.get(key,String.class);

        // 比较传入的令牌与Redis中存储的令牌是否一致
        return token.equals(storedToken);
    }

    /**
     * 验证刷新令牌的有效性。
     * <p>
     * 该方法通过从Redis中获取存储的刷新令牌，并与传入的令牌进行比较，以验证其有效性。
     *
     * @param username 用户名，用于构建Redis中的键。
     * @param token 需要验证的刷新令牌。
     * @param deviceId 设备ID，用于构建Redis中的键。
     * @return 如果传入的令牌与Redis中存储的令牌一致，则返回true；否则返回false。
     */
    public boolean validateRefreshToken(String username, String token, String deviceId) {
        // 构建Redis中的键，格式为：REFRESH_TOKEN_PREFIX + username + ":" + deviceId
        String key = RedisConstants.REFRESH_TOKEN_PREFIX + username + ":" + deviceId;

        // 从Redis中获取存储的刷新令牌
        String storedToken = redisUtils.get(key,String.class);

        // 比较传入的令牌与Redis中存储的令牌是否一致
        return token.equals(storedToken);
    }

    /**
     * 获取指定用户所有设备的访问令牌。
     * <p>
     * 该方法通过构造Redis的键模式，查询与指定用户相关的所有设备的访问令牌。
     * 键模式由Redis常量前缀、用户名和通配符组成，用于匹配所有相关设备的访问令牌。
     *
     * @param username 用户名，用于构造Redis键模式，查找该用户的所有设备访问令牌。
     * @return 返回一个包含所有匹配的访问令牌的Set集合。如果未找到匹配的令牌，则返回空集合。
     */
    public List<DeviceTokenInfo> getAllUserDevices(String username) {
        String userDeviceKey = RedisConstants.USER_DEVICE_PREFIX + username;
    
        Set<Object> deviceIds = redisUtils.sGet(userDeviceKey);
        if (deviceIds == null || deviceIds.isEmpty()) {
            return Collections.emptyList();
        }
    
        return deviceIds.stream()
                .map(deviceId -> {
                    DeviceTokenInfo tokenInfo = new DeviceTokenInfo();
                    tokenInfo.setDeviceId(deviceId.toString());
                    return tokenInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 移除设备绑定
     */
    public void removeDeviceBinding(String username, String deviceId) {
        // 1. 删除访问令牌
        String accessTokenKey = RedisConstants.ACCESS_TOKEN_PREFIX + username + ":" + deviceId;
        redisUtils.del(accessTokenKey);

        // 2. 删除刷新令牌
        String refreshTokenKey = RedisConstants.REFRESH_TOKEN_PREFIX + username + ":" + deviceId;
        redisUtils.del(refreshTokenKey);

        // 3. 移除设备映射
        String userDeviceKey = RedisConstants.USER_DEVICE_PREFIX + username;
        redisUtils.sRemove(userDeviceKey, deviceId);
    }

    /**
     * 生成设备ID字符串，用于唯一标识前端设备。
     * 该函数通过结合请求的User-Agent、客户端的IP地址以及一个随机生成的UUID，
     * 使用MD5算法生成一个唯一的设备ID。
     *
     * @param request HttpServletRequest对象，包含客户端的请求信息
     * @return 生成的设备ID字符串，长度为32位的MD5哈希值
     */
    public String generateDeviceId(HttpServletRequest request) {
        // 从请求头中获取User-Agent信息，用于标识客户端浏览器和操作系统
        String userAgent = request.getHeader("User-Agent");
        // 获取客户端的IP地址，用于标识客户端的位置
        String ipAddress = request.getRemoteAddr();
        // 生成一个随机的UUID，增加设备ID的唯一性
        String randomPart = UUID.randomUUID().toString();

        // 将User-Agent、IP地址和随机UUID拼接，并使用MD5算法生成哈希值作为设备ID
        return DigestUtils.md5DigestAsHex(
                (userAgent + ipAddress + randomPart).getBytes()
        );
    }

}

