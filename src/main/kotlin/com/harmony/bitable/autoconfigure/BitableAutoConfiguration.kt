package com.harmony.bitable.autoconfigure

import com.harmony.bitable.autoconfigure.BitableAutoConfiguration.BitableClientWithRateLimiterRegistry
import com.harmony.bitable.autoconfigure.BitableAutoConfiguration.BitableClientWithoutRateLimiterRegistry
import com.harmony.bitable.oapi.BitableApi
import com.harmony.bitable.oapi.BitableRecordApi
import com.harmony.bitable.oapi.bitable.BitableApiImpl
import com.harmony.bitable.oapi.bitable.BitableRecordApiImpl
import com.harmony.bitable.oapi.ratelimiter.RateLimiterInvocationHandler
import com.lark.oapi.Client
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.util.ClassUtils
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

@AutoConfigureAfter(
    value = [LarkAutoConfiguration::class],
    name = ["io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration"]
)
@ConditionalOnBean(Client::class)
@EnableConfigurationProperties(BitableProperties::class)
@Import(BitableClientWithRateLimiterRegistry::class, BitableClientWithoutRateLimiterRegistry::class)
class BitableAutoConfiguration {

    @ConditionalOnMissingBean(type = ["io.github.resilience4j.ratelimiter.RateLimiterRegistry"])
    class BitableClientWithoutRateLimiterRegistry(
        private val properties: BitableProperties,
        private val larkClient: Client
    ) {

        @Bean
        @ConditionalOnMissingBean(BitableApi::class)
        fun bitableApi(): BitableApi = BitableApiImpl(larkClient, properties.defaultPageSize)

        @Bean
        @ConditionalOnMissingBean(BitableRecordApi::class)
        fun bitableRecordApi(): BitableRecordApi = BitableRecordApiImpl(larkClient, properties.defaultPageSize)

    }

    @ConditionalOnClass(RateLimiterRegistry::class)
    @ConditionalOnBean(RateLimiterRegistry::class)
    class BitableClientWithRateLimiterRegistry(
        private val properties: BitableProperties,
        private val larkClient: Client,
        private val rateLimiterRegistry: RateLimiterRegistry
    ) {

        private val log: Logger = LoggerFactory.getLogger(BitableClientWithRateLimiterRegistry::class.java)

        @Bean
        @ConditionalOnMissingBean(BitableApi::class)
        fun bitableApi(): BitableApi {
            val bitableApi = BitableApiImpl(larkClient, properties.defaultPageSize)
            return proxyWithRateLimiter(bitableApi, BitableApi::class, rateLimiterRegistry)
        }

        @Bean
        @ConditionalOnMissingBean(BitableRecordApi::class)
        fun bitableRecordApi(
        ): BitableRecordApi {
            val bitableRecordApi = BitableRecordApiImpl(larkClient, properties.defaultPageSize)
            return proxyWithRateLimiter(bitableRecordApi, BitableRecordApi::class, rateLimiterRegistry)
        }

        private fun <T : Any> proxyWithRateLimiter(
            target: T,
            interfaces: KClass<T>,
            rateLimiterRegistry: RateLimiterRegistry
        ): T {
            log.info("Create rate-limiter {}", interfaces.simpleName)
            val loader = interfaces::class.java.classLoader ?: ClassUtils.getDefaultClassLoader()
            return Proxy.newProxyInstance(
                loader,
                arrayOf(interfaces.java),
                RateLimiterInvocationHandler(target, rateLimiterRegistry)
            ) as T
        }

    }

}

