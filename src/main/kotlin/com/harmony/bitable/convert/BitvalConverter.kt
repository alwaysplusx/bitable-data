package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
interface BitvalConverter {

    /**
     * 验证是否支持该字段读取
     */
    fun canHandle(property: BitablePersistentProperty): Boolean

    /**
     * 从飞书 appTableRecord 中读取字段并解析为 bitity 字段格式值
     */
    fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any?

    /**
     * 从 Bitity 中读取字段并解析为飞书多维表格字段值格式，最终写入到飞书的记录中
     */
    fun convertAndWrite(value: Any?, property: BitablePersistentProperty, record: AppTableRecord)

}