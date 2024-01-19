package com.harmony.bitable.oapi

import com.lark.oapi.Client
import com.lark.oapi.core.Config
import com.lark.oapi.core.cache.ICache
import com.lark.oapi.core.httpclient.IHttpTransport
import java.lang.IllegalArgumentException
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * 构建 lark client
 * @author wuxin
 */
class LarkClientBuilder(private var config: Config = Config()) {

    fun withConfig(config: Config): LarkClientBuilder {
        this.config = config
        return this
    }

    fun customize(customizer: (Config) -> Unit): LarkClientBuilder {
        customizer.invoke(config)
        return this
    }

    fun setAppId(appId: String): LarkClientBuilder {
        this.config.appId = appId
        return this
    }


    fun setAppSecret(appSecret: String): LarkClientBuilder {
        this.config.appSecret = appSecret
        return this
    }

    fun setCache(cache: ICache?): LarkClientBuilder {
        this.config.cache = cache
        return this
    }

    fun setRequestTimeout(timeout: Duration?): LarkClientBuilder {
        if (timeout != null) {
            this.config.requestTimeOut = timeout.toMillis()
            this.config.timeOutTimeUnit = TimeUnit.MILLISECONDS
        } else {
            this.config.requestTimeOut = 0
            this.config.timeOutTimeUnit = null
        }
        return this
    }

    fun setHttpTransport(transport: IHttpTransport?): LarkClientBuilder {
        this.config.httpTransport = transport
        return this
    }

    fun build(): Client {
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

}
