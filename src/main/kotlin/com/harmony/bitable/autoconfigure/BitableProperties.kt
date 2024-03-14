package com.harmony.bitable.autoconfigure

import com.lark.oapi.core.Config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "bitable")
class BitableProperties {

    lateinit var appToken: String

    var defaultPageSize = 10

    @NestedConfigurationProperty
    var client = Config()

}
