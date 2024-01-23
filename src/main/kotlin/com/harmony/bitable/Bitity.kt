package com.harmony.bitable

import org.springframework.data.mapping.model.Property

/**
 * 基于实体解析得出的于飞书表格映射关系
 */
interface Bitity<T : Any> : Iterable<BitityField> {

    /**
     * 实体名称(bitable name or class simpleName)
     */
    fun getName(): String

    /**
     * 实体类
     */
    fun getType(): Class<T>

    /**
     * 实体所拥有的字段
     */
    fun getFields(): List<BitityField>

    /**
     * 依据 property 获取实体
     */
    fun getField(property: Property): BitityField?

}
