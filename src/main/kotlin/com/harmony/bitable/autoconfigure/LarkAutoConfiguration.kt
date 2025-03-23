package com.harmony.bitable.autoconfigure

import com.harmony.bitable.autoconfigure.LarkAutoConfiguration.ClientWithRateLimiterRegistry
import com.harmony.bitable.autoconfigure.LarkAutoConfiguration.ClientWithoutRateLimiterRegistry
import com.harmony.bitable.oapi.ratelimiter.RateLimiterMethodInterceptor
import com.lark.oapi.Client
import com.lark.oapi.core.Config
import com.lark.oapi.core.cache.ICache
import com.lark.oapi.core.httpclient.IHttpTransport
import com.lark.oapi.service.bitable.v1.resource.AppTable
import com.lark.oapi.service.bitable.v1.resource.AppTableField
import com.lark.oapi.service.bitable.v1.resource.AppTableRecord
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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

@AutoConfigureAfter(
    value = [RedisAutoConfiguration::class],
    name = ["io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration"]
)
@ConditionalOnClass(Client::class)
@ConditionalOnProperty(prefix = "lark.client", name = ["app-id", "app-secret"])
@EnableConfigurationProperties(LarkProperties::class)
@Import(ClientWithRateLimiterRegistry::class, ClientWithoutRateLimiterRegistry::class)
class LarkAutoConfiguration(private val properties: LarkProperties) {

    @Bean
    @ConditionalOnProperty(prefix = "lark.client", name = ["app-id", "app-secret"])
    @ConditionalOnMissingBean(Client::class)
    fun larkClient(
        @Autowired(required = false) cache: ICache?,
        @Autowired(required = false) httpTransient: IHttpTransport?,
    ): Client {
        val config = properties.client
        requireNotNull(config.appId) { "lark appId not allow null" }
        requireNotNull(config.appSecret) { "lark appSecret not allow null" }

        val builder = Client.newBuilder(config.appId, config.appSecret)
        if (config.isDisableTokenCache) {
            builder.disableTokenCache()
        }
        return builder.appType(config.appType)
            .helpDeskCredential(config.helpDeskID, config.helpDeskToken)
            .logReqAtDebug(config.isLogReqAtDebug)
            .openBaseUrl(config.baseUrl)
            .tokenCache(config.cache)
            .httpTransport(config.httpTransport)
            .requestTimeout(config.requestTimeOut, config.timeOutTimeUnit)
            .build()
    }

    @ConditionalOnMissingBean(type = ["io.github.resilience4j.ratelimiter.RateLimiterRegistry"])
    class ClientWithoutRateLimiterRegistry(private val client: Client) {

        @Bean
        fun appTable(): AppTable = client.bitable().appTable()

        @Bean
        fun appTableField(): AppTableField = client.bitable().appTableField()

        @Bean
        fun appTableRecord(): AppTableRecord = client.bitable().appTableRecord()

    }

    @ConditionalOnClass(RateLimiterRegistry::class)
    @ConditionalOnBean(RateLimiterRegistry::class)
    class ClientWithRateLimiterRegistry(
        private val client: Client,
        private val properties: LarkProperties,
        private val rateLimiterRegistry: RateLimiterRegistry
    ) {

        private val log: Logger = LoggerFactory.getLogger(ClientWithRateLimiterRegistry::class.java)

        @Bean
        fun appTable(): AppTable = proxyWithRateLimiter(client.bitable().appTable())

        @Bean
        fun appTableField(): AppTableField = proxyWithRateLimiter(client.bitable().appTableField())

        @Bean
        fun appTableRecord(): AppTableRecord = proxyWithRateLimiter(client.bitable().appTableRecord())

        private fun <T : Any> proxyWithRateLimiter(target: T): T {
            log.info("Create rate-limiter for {}", target.javaClass.simpleName)
            val enhancer = Enhancer()
            enhancer.setSuperclass(target.javaClass)
            enhancer.setCallback(RateLimiterMethodInterceptor(target, rateLimiterRegistry))
            return enhancer.create(arrayOf(Config::class.java), arrayOf(properties.client)) as T
        }

    }

}