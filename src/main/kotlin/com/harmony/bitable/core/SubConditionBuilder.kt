package com.harmony.bitable.core

import com.harmony.bitable.mapping.BitablePersistentEntity
import com.lark.oapi.service.bitable.v1.model.ChildrenFilter

class SubConditionBuilder<T>(private val conjunction: Conjunction = Conjunction.AND, rootType: Class<T>) :
    AbstractConditionBuilder<T>(rootType) {

    @Deprecated(
        "Nested and/or not allowed", level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith("throw UnsupportedOperationException(\"Nested and/or not allowed\")")
    )
    fun and(block: Any?.() -> Unit) {
        throw UnsupportedOperationException("Nested and/or not allowed")
    }

    @Deprecated(
        "Nested and/or not allowed", level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith("throw UnsupportedOperationException(\"Nested and/or not allowed\")")
    )
    fun or(block: Any?.() -> Unit) {
        throw UnsupportedOperationException("Nested and/or not allowed")
    }

    internal fun build(persistentEntity: BitablePersistentEntity<T>): ChildrenFilter {
        return ChildrenFilter.newBuilder()
            .conjunction(conjunction.childrenValue)
            .conditions(buildSearchConditions(persistentEntity).toTypedArray())
            .build()
    }

}