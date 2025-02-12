package com.harmony.bitable.autoconfigure

import com.harmony.bitable.oapi.LarkClientBuilder
import com.lark.oapi.Client
import com.lark.oapi.core.cache.ICache
import com.lark.oapi.core.httpclient.IHttpTransport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfigureAfter(RedisAutoConfiguration::class)
@ConditionalOnClass(Client::class)
@EnableConfigurationProperties(LarkProperties::class)
class LarkAutoConfiguration(private val properties: LarkProperties) {

    @Bean
    @ConditionalOnProperty(prefix = "lark.client", name = ["app-id", "app-secret"])
    @ConditionalOnMissingBean(Client::class)
    fun larkClient(
        @Autowired(required = false) cache: ICache?,
        @Autowired(required = false) httpTransient: IHttpTransport?,
    ): Client {
        return LarkClientBuilder()
            .withConfig(properties.client)
            .setCache(cache)
            .setHttpTransport(httpTransient)
            .build()
    }

}