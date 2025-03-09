package com.harmony.bitable.convert

/**
 * 用于代表一个飞书表的一条记录
 *
 * @author wuxin
 */
data class BitfieldValueMap(
    /**
     * 飞书表-实体类型
     */
    val domainType: Class<*>,
    /**
     * 记录中各个列的名称与其对应的值
     */
    val valueMap: Map<String, Any?>
)