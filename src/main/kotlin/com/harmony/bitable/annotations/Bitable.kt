package com.harmony.bitable.annotations

import org.springframework.core.annotation.AliasFor
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * 映射为[飞书多维表格](https://open.feishu.cn/document/server-docs/docs/bitable-v1/bitable-overview)
 */
@Target(ANNOTATION_CLASS, CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bitable(

    @get:AliasFor("name")
    val value: String = "",

    /**
     * 多维表格名称, 同一 [app_token](https://open.feishu.cn/document/server-docs/docs/bitable-v1/bitable-overview) 下名称唯一
     */
    @get:AliasFor("value")
    val name: String = ""

)
