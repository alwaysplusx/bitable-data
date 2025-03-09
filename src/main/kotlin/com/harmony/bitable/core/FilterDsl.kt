package com.harmony.bitable.core

import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.enums.ChildrenFilterConjunctionEnum
import com.lark.oapi.service.bitable.v1.enums.ConditionOperatorEnum
import com.lark.oapi.service.bitable.v1.enums.FilterInfoConjunctionEnum
import org.springframework.util.ClassUtils
import java.io.Serializable
import java.lang.reflect.Method
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KCallable
import kotlin.reflect.KClass

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

@FunctionalInterface
interface NameFunction<T, R> : java.util.function.Function<T, R>, Serializable

data class NameInformation(val name: String, val owner: Class<*>) {

    companion object {
        fun of(column: KCallable<*>): NameInformation {
            val owner = ((column as CallableReference).owner as KClass<*>).java
            return NameInformation(resolveName(column.name), owner)
        }

        @JvmStatic
        fun of(column: NameFunction<*, *>): NameInformation {
            val lambda = read(column)
            val owner = ClassUtils.forName(lambda.implClass.replace("/", "."), column.javaClass.classLoader)
            return NameInformation(resolveName(lambda.implMethodName), owner)
        }

        private fun read(func: NameFunction<*, *>): java.lang.invoke.SerializedLambda {
            try {
                val method: Method = func.javaClass.getDeclaredMethod("writeReplace")
                method.setAccessible(true)
                return method.invoke(func) as java.lang.invoke.SerializedLambda
            } catch (e: Exception) {
                throw e
            }
        }

        private fun resolveName(name: String): String {
            return name.substringAfter("get").replaceFirstChar { it.lowercase() }
        }

    }

    override fun toString(): String {
        return "NameInformation(${owner.simpleName}.${name})"
    }

}