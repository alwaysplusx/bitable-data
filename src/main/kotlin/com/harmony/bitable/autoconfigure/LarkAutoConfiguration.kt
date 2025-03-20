package com.harmony.bitable.autoconfigure

import com.harmony.bitable.autoconfigure.LarkAutoConfiguration.ClientWithRateLimiterRegistry
import com.harmony.bitable.autoconfigure.LarkAutoConfiguration.ClientWithoutRateLimiterRegistry
import com.harmony.bitable.oapi.ratelimiter.RateLimiterMethodInterceptor
import com.lark.oapi.Client
import com.lark.oapi.core.Config
import com.lark.oapi.service.bitable.v1.resource.AppTable
import com.lark.oapi.service.bitable.v1.resource.AppTableField
import com.lark.oapi.service.bitable.v1.resource.AppTableRecord
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cglib.proxy.Enhancer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.util.ClassUtils

@AutoConfigureAfter(
    value = [RedisAutoConfiguration::class],
    name = ["io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration"]
)
@ConditionalOnClass(Client::class)
@ConditionalOnProperty(prefix = "lark.client", name = ["app-id", "app-secret"])
@EnableConfigurationProperties(LarkProperties::class)
@Import(ClientWithRateLimiterRegistry::class, ClientWithoutRateLimiterRegistry::class)
class LarkAutoConfiguration {

    @ConditionalOnMissingBean(type = ["io.github.resilience4j.ratelimiter.RateLimiterRegistry"])
    class ClientWithoutRateLimiterRegistry(private val properties: LarkProperties) {

        @Bean
        fun appTable(): AppTable = AppTable(properties.client)

        @Bean
        fun appTableField(): AppTableField = AppTableField(properties.client)

        @Bean
        fun appTableRecord(): AppTableRecord = AppTableRecord(properties.client)

    }

    @ConditionalOnClass(RateLimiterRegistry::class)
    @ConditionalOnBean(RateLimiterRegistry::class)
    class ClientWithRateLimiterRegistry(
        private val properties: LarkProperties,
        private val rateLimiterRegistry: RateLimiterRegistry
    ) {

        private val log: Logger = LoggerFactory.getLogger(ClientWithRateLimiterRegistry::class.java)

        @Bean
        fun appTable(): AppTable = proxyWithRateLimiter(AppTable::class.java)

        @Bean
        fun appTableField(): AppTableField = proxyWithRateLimiter(AppTableField::class.java)

        @Bean
        fun appTableRecord(): AppTableRecord = proxyWithRateLimiter(AppTableRecord::class.java)

        private fun <T : Any> proxyWithRateLimiter(targetClass: Class<T>): T {
            log.info("Create rate-limiter for {}", targetClass.simpleName)

            val constructor = ClassUtils.getConstructorIfAvailable(targetClass, Config::class.java)
                ?: throw IllegalStateException("Can't find constructor for ${targetClass.simpleName}")

            val config = properties.client
            val enhancer = Enhancer()
            enhancer.setSuperclass(targetClass)
            enhancer.setCallback(RateLimiterMethodInterceptor(constructor.newInstance(config), rateLimiterRegistry))
            return enhancer.create(arrayOf(Config::class.java), arrayOf(properties.client)) as T
        }

    }

}