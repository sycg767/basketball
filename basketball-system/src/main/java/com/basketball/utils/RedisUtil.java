package com.basketball.utils;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;

/**
 * Redis工具类（暂时注释）
 *
 * @author Basketball Team
 * @date 2025-09-30
 */
//@Component
public class RedisUtil {

//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    /**
//     * 设置缓存
//     */
//    public void set(String key, Object value) {
//        redisTemplate.opsForValue().set(key, value);
//    }
//
//    /**
//     * 设置缓存（带过期时间）
//     */
//    public void set(String key, Object value, long timeout) {
//        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
//    }
//
//    /**
//     * 获取缓存
//     */
//    public Object get(String key) {
//        return redisTemplate.opsForValue().get(key);
//    }
//
//    /**
//     * 删除缓存
//     */
//    public Boolean delete(String key) {
//        return redisTemplate.delete(key);
//    }
//
//    /**
//     * 判断key是否存在
//     */
//    public Boolean hasKey(String key) {
//        return redisTemplate.hasKey(key);
//    }
//
//    /**
//     * 设置过期时间
//     */
//    public Boolean expire(String key, long timeout) {
//        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
//    }
}