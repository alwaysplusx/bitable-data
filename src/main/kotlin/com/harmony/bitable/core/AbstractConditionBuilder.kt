package com.harmony.bitable.core

import com.harmony.bitable.mapping.BitablePersistentEntity
import com.lark.oapi.core.utils.Jsons
import com.lark.oapi.service.bitable.v1.enums.ConditionOperatorEnum
import com.lark.oapi.service.bitable.v1.enums.ConditionOperatorEnum.*
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction1
import kotlin.reflect.KMutableProperty1

abstract class AbstractConditionBuilder<T>(protected val rootType: Class<T>) {

    private val allConditions = mutableListOf<Condition>()

    // for javaBean

    infix fun <R> KFunction1<T, R>.`is`(value: Any): Condition {
        return addCondition(this, value, OPERATORIS)
    }

    infix fun <R> KFunction1<T, R>.isNot(value: Any): Condition {
        return addCondition(this, value, OPERATORISNOT)
    }

    infix fun <R> KFunction1<T, R>.contains(value: Any): Condition {
        return addCondition(this, value, OPERATORCONTAINS)
    }

    infix fun <R> KFunction1<T, R>.notContains(value: Any): Condition {
        return addCondition(this, value, OPERATORDOESNOTCONTAIN)
    }

    infix fun <R> KFunction1<T, R>.isEmpty(value: Boolean): Condition {
        val op = if (value) OPERATORISEMPTY else OPERATORISNOTEMPTY
        return addCondition(this, null, op)
    }

    infix fun <R> KFunction1<T, R>.isGreater(value: Any?): Condition {
        return addCondition(this, value, OPERATORISGREATER)
    }

    infix fun <R> KFunction1<T, R>.isGreaterEqual(value: Any?): Condition {
        return addCondition(this, value, OPERATORISGREATEREQUAL)
    }

    infix fun <R> KFunction1<T, R>.isLess(value: Any?): Condition {
        return addCondition(this, value, OPERATORISLESS)
    }

    infix fun <R> KFunction1<T, R>.isLessEqual(value: Any?): Condition {
        return addCondition(this, value, OPERATORISLESSEQUAL)
    }

    infix fun <R> KFunction1<T, R>.like(value: Any?): Condition {
        return addCondition(this, value, OPERATORLIKE)
    }

    infix fun <R> KFunction1<T, R>.`in`(value: Any?): Condition {
        return addCondition(this, value, OPERATORIN)
    }

    // for kotlin KClass

    infix fun <R> KMutableProperty1<T, R>.`is`(value: Any): Condition {
        return addCondition(this, value, OPERATORIS)
    }

    infix fun <R> KMutableProperty1<T, R>.isNot(value: Any): Condition {
        return addCondition(this, value, OPERATORISNOT)
    }

    infix fun <R> KMutableProperty1<T, R>.contains(value: Any): Condition {
        return addCondition(this, value, OPERATORCONTAINS)
    }

    infix fun <R> KMutableProperty1<T, R>.notContains(value: Any): Condition {
        return addCondition(this, value, OPERATORDOESNOTCONTAIN)
    }

    infix fun <R> KMutableProperty1<T, R>.isEmpty(value: Boolean): Condition {
        val op = if (value) OPERATORISEMPTY else OPERATORISNOTEMPTY
        return addCondition(this, null, op)
    }

    infix fun <R> KMutableProperty1<T, R>.isGreater(value: Any?): Condition {
        return addCondition(this, value, OPERATORISGREATER)
    }

    infix fun <R> KMutableProperty1<T, R>.isGreaterEqual(value: Any?): Condition {
        return addCondition(this, value, OPERATORISGREATEREQUAL)
    }

    infix fun <R> KMutableProperty1<T, R>.isLess(value: Any?): Condition {
        return addCondition(this, value, OPERATORISLESS)
    }

    infix fun <R> KMutableProperty1<T, R>.isLessEqual(value: Any?): Condition {
        return addCondition(this, value, OPERATORISLESSEQUAL)
    }

    infix fun <R> KMutableProperty1<T, R>.like(value: Any?): Condition {
        return addCondition(this, value, OPERATORLIKE)
    }

    infix fun <R> KMutableProperty1<T, R>.`in`(value: Any?): Condition {
        return addCondition(this, value, OPERATORIN)
    }

    private fun addCondition(column: KCallable<*>, value: Any?, op: ConditionOperatorEnum): Condition {
        val condition = Condition(NameInformation.of(column), value, op)
        allConditions.add(condition)
        return condition
    }

    internal fun buildSearchConditions(persistentEntity: BitablePersistentEntity<*>): List<SearchCondition> {
        return allConditions.map {
            val persistentProperty = persistentEntity.getFieldByName(it.name)
            SearchCondition.newBuilder()
                .fieldName(persistentProperty.getBitfieldName())
                .value(formatSearchValues(it.value))
                .operator(it.op)
                .build()
        }
    }

    private fun formatSearchValues(value: Any?): Array<String>? {
        return when (value) {
            null -> arrayOf()
            is String -> arrayOf(value)
            is ValueSupplier -> value.get()
            is Array<*> -> value.map { Jsons.DEFAULT.toJson(it) }.toTypedArray()
            is Collection<*> -> value.map { Jsons.DEFAULT.toJson(it) }.toTypedArray()
            else -> Jsons.DEFAULT.toJson(value).let { arrayOf(it) }
        }
    }

}