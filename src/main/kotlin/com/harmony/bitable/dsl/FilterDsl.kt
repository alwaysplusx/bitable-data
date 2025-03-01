package com.harmony.bitable.dsl

import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.enums.ChildrenFilterConjunctionEnum
import com.lark.oapi.service.bitable.v1.enums.ConditionOperatorEnum
import com.lark.oapi.service.bitable.v1.enums.FilterInfoConjunctionEnum
import java.io.Serializable
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1

fun <T> BitablePersistentEntity<T>.getFieldByName(name: NameInformation): BitablePersistentProperty {
    return this.first { it.getSimpleName() == name.name }
}

typealias SearchCondition = com.lark.oapi.service.bitable.v1.model.Condition

typealias SearchSort = com.lark.oapi.service.bitable.v1.model.Sort

data class Sort(val name: NameInformation, val desc: Boolean)

data class Condition(val name: NameInformation, val value: Any?, val op: ConditionOperatorEnum)

enum class Conjunction(val value: FilterInfoConjunctionEnum, val childrenValue: ChildrenFilterConjunctionEnum) {

    AND(FilterInfoConjunctionEnum.CONJUNCTIONAND, ChildrenFilterConjunctionEnum.CONJUNCTIONAND),
    OR(FilterInfoConjunctionEnum.CONJUNCTIONOR, ChildrenFilterConjunctionEnum.CONJUNCTIONOR)

}

interface NameFunction<T, R> : java.util.function.Function<T, R>, KFunction1<T, R>, Serializable

data class NameInformation(val name: String, val owner: Class<*>) {

    companion object {
        fun of(column: KCallable<*>): NameInformation {
            val name = column.name.substringAfter("get").replaceFirstChar { it.lowercase() }
            val owner = ((column as CallableReference).owner as KClass<*>).java
            return NameInformation(name, owner)
        }
    }

    override fun toString(): String {
        return "NameInformation(${owner.simpleName}.${name})"
    }

}