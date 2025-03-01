package com.harmony.bitable

import org.springframework.data.mapping.model.Property

/**
 * 基于实体解析得出的于飞书表格映射关系
 */
data class Bitity<T>(
    /**
     * 飞书 appToken 下的 tableName
     */
    val name: String,
    /**
     * 实体类型
     */
    val type: Class<T>,
    /**
     * 飞书表格地址
     */
    val address: BitableAddress,
    /**
     * 表格字段信息， 实体字段&飞书列的聚合关系
     */
    val fields: List<BitityField>,
) : Iterable<BitityField> {

    private val fieldCache: Map<Property, BitityField> = fields.associateBy { it.property }

    /**
     * 依据 property 获取实体
     */
    fun getField(property: Property): BitityField? = fieldCache[property]

    override fun iterator(): Iterator<BitityField> = fields.iterator()

}
