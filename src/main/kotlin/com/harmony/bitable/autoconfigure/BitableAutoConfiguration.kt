package com.harmony.bitable.autoconfigure

import com.harmony.bitable.oapi.LarkClientBuilder
import com.harmony.bitable.oapi.bitable.BitableApi
import com.harmony.bitable.oapi.bitable.BitableRecordApi
import com.lark.oapi.Client
import com.lark.oapi.core.cache.ICache
import com.lark.oapi.core.httpclient.IHttpTransport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn

@ConditionalOnClass(Client::class)
@EnableConfigurationProperties(BitableProperties::class)
class BitableAutoConfiguration(private val properties: BitableProperties) {

    @Bean
    @ConditionalOnProperty(prefix = "bitable.client", name = ["app-id", "app-secret"])
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

    @Bean
    @DependsOn("larkClient")
    @ConditionalOnBean(Client::class)
    @ConditionalOnMissingBean(BitableApi::class)
    fun bitableApi(client: Client) = BitableApi(client, properties.defaultPageSize)

    @Bean
    @DependsOn("larkClient")
    @ConditionalOnBean(Client::class)
    @ConditionalOnMissingBean(BitableRecordApi::class)
    fun bitableRecordApi(client: Client) = BitableRecordApi(client, properties.defaultPageSize)

}
