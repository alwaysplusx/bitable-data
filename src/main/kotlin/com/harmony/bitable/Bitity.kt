package com.harmony.bitable

import org.springframework.data.mapping.model.Property

/**
 * 基于实体解析得出的于飞书表格映射关系
 */
data class Bitity<T>(
    val name: String,
    val type: Class<T>,
    val fields: List<BitityField>,
) : Iterable<BitityField> {

    private val fieldCache: Map<Property, BitityField> = fields.associateBy { it.property }

    /**
     * 依据 property 获取实体
     */
    fun getField(property: Property): BitityField? = fieldCache[property]

    override fun iterator(): Iterator<BitityField> = fields.iterator()

}
