package com.harmony.bitable.autoconfigure

import com.lark.oapi.core.Config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "lark")
class LarkProperties {

    @NestedConfigurationProperty
    var client = Config()

}