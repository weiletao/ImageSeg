package com.scu.imageseg.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  Redis静态工具类 用于封装StringRedisTemplate类 实现对Redis数据库的基本操作
 * </p>
 *
 * @author wlt
 * @since 2025-03-17
 */

public class RedisUtil {

    public static void set(StringRedisTemplate redisTemplate, String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public static String get(StringRedisTemplate redisTemplate,String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public static void delete(StringRedisTemplate redisTemplate, String key) {
        redisTemplate.delete(key);
    }
}
