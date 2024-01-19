package com.harmony.bitable.oapi

import com.lark.oapi.core.cache.ICache
import java.util.concurrent.TimeUnit
import org.springframework.data.redis.core.StringRedisTemplate

class RedisCache(private val prefix: String, private val redisTemplate: StringRedisTemplate) : ICache {

    override fun get(key: String): String? {
        return redisTemplate.opsForValue().get("$prefix$key")
    }

    override fun set(key: String, value: String, expire: Int, timeUnit: TimeUnit) {
        redisTemplate.opsForValue().set("$prefix$key", value, expire.toLong(), timeUnit)
    }

}
