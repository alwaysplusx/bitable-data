package com.harmony.bitable.dsl

import com.harmony.bitable.core.SearchBodyBuilder
import com.harmony.bitable.core.SearchRequestBuilder
import com.harmony.bitable.dsl.builder.ConditionBuilder
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.lark.oapi.service.bitable.v1.model.FilterInfo
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import kotlin.reflect.KFunction1
import kotlin.reflect.KMutableProperty1

/**
 * 支持飞书按字段条件查询(最多只支持一级子查询)
 * https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/bitable-v1/app-table-record/record-filter-guide
 * @see FilterInfo
 */
abstract class AbstractFilterBuilder<T : Any>(rootType: Class<T>) {

    protected val conditionBuilder: ConditionBuilder<T> = ConditionBuilder(rootType)

    protected var pageSize: Int = 20
    protected var offset: String? = null
    protected var viewId: String? = null
    protected var customizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit = { _, _ -> }

    protected val sorts = mutableListOf<Sort>()
    protected val columns = mutableListOf<NameInformation>()

    abstract fun build(persistentEntity: BitablePersistentEntity<T>): SearchAppTableRecordReq

    fun select(vararg columns: KFunction1<T, *>) {
        columns.mapTo(this.columns) { NameInformation.of(it) }
    }

    fun select(vararg columns: KMutableProperty1<T, *>) {
        columns.mapTo(this.columns) { NameInformation.of(it) }
    }

    fun viewId(viewId: String?) {
        this.viewId = viewId
    }

    fun where(
        conjunction: Conjunction = Conjunction.AND,
        block: ConditionBuilder<T>.() -> Unit
    ) {
        this.conditionBuilder.apply(block)
        this.conditionBuilder.withConjunction(conjunction)
    }

    fun <R> desc(vararg columns: KFunction1<T, R>) {
        columns.mapTo(sorts) { Sort(NameInformation.of(it), true) }
    }

    fun <R> desc(vararg columns: KMutableProperty1<T, R>) {
        columns.mapTo(sorts) { Sort(NameInformation.of(it), true) }
    }

    fun <R> asc(vararg columns: KFunction1<T, R>) {
        columns.mapTo(sorts) { Sort(NameInformation.of(it), false) }
    }

    fun <R> asc(vararg columns: KMutableProperty1<T, R>) {
        columns.mapTo(sorts) { Sort(NameInformation.of(it), false) }
    }

    fun withCustomizer(customizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit) {
        this.customizer = customizer
    }

    protected fun buildSearchSorts(persistentEntity: BitablePersistentEntity<T>): List<SearchSort> {
        return sorts.map {
            val persistentProperty = persistentEntity.getFieldByName(it.name)
            SearchSort.newBuilder().fieldName(persistentProperty.getBitfieldName()).desc(it.desc).build()
        }
    }

}

