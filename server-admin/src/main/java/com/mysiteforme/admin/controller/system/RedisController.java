/**
 * Redis 数据管理控制器
 * 提供查看和管理 Redis 数据的 API
 */
package com.mysiteforme.admin.controller.system;

import com.mysiteforme.admin.annotation.RateLimit;
import com.mysiteforme.admin.annotation.SysLog;
import com.mysiteforme.admin.entity.request.PageListRedisRequest;
import com.mysiteforme.admin.service.RedisService;
import com.mysiteforme.admin.util.LimitType;
import com.mysiteforme.admin.util.MessageConstants;
import com.mysiteforme.admin.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/admin/redis")
@RequiredArgsConstructor
@RateLimit(limit = 30, period = 1, timeUnit = TimeUnit.MINUTES, limitType = LimitType.USER)
public class RedisController {

    private final RedisService redisService;

    /**
     * 获取Redis键并分页显示
     * @param request 分页和过滤参数
     * @return 包含类型、过期时间和值的Redis键列表
     */
    @GetMapping("list")
    public Result list(PageListRedisRequest request) {
        try {
            return Result.success(redisService.getRedisDataPageList(request));
        } catch (Exception e) {
            log.error("获取Redis数据时出错", e);
            return Result.businessMsgError(MessageConstants.Redis.REDIS_GET_VALUE_EXCEPTION,e.getMessage());
        }
    }

    /**
     * 删除Redis键
     * @param key 要删除的Redis键
     * @return 表示成功或失败的结果
     */
    @SysLog(MessageConstants.SysLog.REDIS_DELETE)
    @DeleteMapping("delete")
    public Result delete(@RequestParam(value = "key", required = false) String key) {
        try {
            redisService.deleteRedisData(key);
            return Result.success();
        } catch (Exception e) {
            log.error("删除Redis键时出错: {}", key, e);
            return Result.businessMsgError(MessageConstants.Redis.REDIS_DELETE_VALUE_EXCEPTION,key);
        }
    }


}
