package com.harmony.bitable.dsl.builder

import com.harmony.bitable.dsl.Conjunction
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.lark.oapi.service.bitable.v1.model.FilterInfo
import org.springframework.util.ObjectUtils

open class ConditionBuilder<T>(
    rootType: Class<T>,
    private var conjunction: Conjunction = Conjunction.AND,
) : AbstractConditionBuilder<T>(rootType) {

    /**
     * 子查询
     */
    private val subConditions = mutableListOf<SubConditionBuilder<T>>()

    /**
     * 修改当前 builder 的 conjunction
     */
    fun withConjunction(conjunction: Conjunction) {
        this.conjunction = conjunction
    }

    fun and(block: SubConditionBuilder<T>.() -> Unit) {
        val subBuilder = SubConditionBuilder(Conjunction.AND, rootType).apply(block)
        subConditions.add(subBuilder)
    }

    fun or(block: SubConditionBuilder<T>.() -> Unit) {
        val subBuilder = SubConditionBuilder(Conjunction.OR, rootType).apply(block)
        subConditions.add(subBuilder)
    }

    fun buildSearchFilter(persistentEntity: BitablePersistentEntity<T>): FilterInfo {
        val builder = FilterInfo.newBuilder()
            .conjunction(conjunction.value)
            .conditions(buildSearchConditions(persistentEntity).toTypedArray())
        if (!ObjectUtils.isEmpty(subConditions)) {
            builder.children(subConditions.map { it.build(persistentEntity) }.toTypedArray())
        }
        return builder.build()
    }

}