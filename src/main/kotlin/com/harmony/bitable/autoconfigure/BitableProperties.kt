package com.harmony.bitable.autoconfigure

import com.lark.oapi.core.Config
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bitable")
class BitableProperties {

    lateinit var appToken: String

    var defaultPageSize = 10

    var client = Config()

}
