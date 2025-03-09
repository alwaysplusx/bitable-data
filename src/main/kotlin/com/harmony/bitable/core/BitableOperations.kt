package com.harmony.bitable.core

import com.harmony.bitable.dsl.CountFilterBuilder
import com.harmony.bitable.dsl.SearchFilterBuilder
import com.harmony.bitable.dsl.SingleResultFilterBuilder
import com.harmony.bitable.dsl.UpdateBuilder
import com.harmony.bitable.oapi.Pageable
import com.harmony.bitable.oapi.cursor.PageCursor
import com.harmony.bitable.oapi.cursor.firstElementOrNull
import com.harmony.bitable.oapi.cursor.toElementList
import com.harmony.bitable.utils.SearchUtils

/**
 * 支持 bitable 数据的操作
 */
interface BitableOperations {

    fun <T : Any> insert(instance: T): T

    fun <T : Any> insertBatch(instances: Iterable<T>, domainType: Class<T>): Iterable<T>

    fun <T : Any> update(instance: T): T

    fun <T : Any> updateById(
        recordId: String,
        domainType: Class<T>,
        updateCustomizer: (req: UpdateRequestBuilder, body: UpdateBodyBuilder) -> Unit = { _, _ -> }
    )

    fun <T : Any> updateById(recordId: String, domainType: Class<T>, block: UpdateBuilder<T>.() -> Unit)

    fun <T : Any> deleteAll(domainType: Class<T>)

    fun <T : Any> delete(instance: T): Boolean

    fun <T : Any> deleteById(recordId: String, domainType: Class<T>): Boolean

    fun <T : Any> deleteAllById(recordIds: Iterable<String>, domainType: Class<T>): Map<String, Boolean>

    fun <T : Any> findById(recordId: String, domainType: Class<T>): T?

    fun <T : Any> findAll(domainType: Class<T>): Iterable<T> = scan(domainType).toElementList()

    fun <T : Any> findAllById(recordIds: Iterable<String>, domainType: Class<T>): Iterable<T>

    /**
     * unique result or null
     */
    fun <T : Any> findOne(
        domainType: Class<T>,
        searchCustomizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit = { _, _ -> }
    ): T?

    /**
     * unique result or null
     */
    fun <T : Any> findOne(domainType: Class<T>, block: SingleResultFilterBuilder<T>.() -> Unit): T?

    /**
     * first result or null
     */
    fun <T : Any> findFirst(
        domainType: Class<T>,
        searchCustomizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit = { _, _ -> }
    ): T? {
        val finalCustomizer = SearchUtils.all(searchCustomizer) { req, _ ->
            req.pageSize(1)
        }
        return scan(domainType, finalCustomizer).firstElementOrNull()
    }

    /**
     * first result or null
     */
    fun <T : Any> findFirst(domainType: Class<T>, block: SingleResultFilterBuilder<T>.() -> Unit): T?

    fun <T : Any> count(
        domainType: Class<T>,
        searchCustomizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit = { _, _ -> }
    ): Long

    fun <T : Any> count(domainType: Class<T>, block: CountFilterBuilder<T>.() -> Unit): Long

    fun <T : Any> scan(domainType: Class<T>, pageable: Pageable): PageCursor<T> {
        return scan(domainType) { req, _ ->
            req.pageSize(pageable.pageSize)
            req.pageToken(pageable.pageToken)
        }
    }

    fun <T : Any> scan(
        domainType: Class<T>,
        searchCustomizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit = { _, _ -> }
    ): PageCursor<T>

    fun <T : Any> scan(domainType: Class<T>, block: SearchFilterBuilder<T>.() -> Unit): PageCursor<T>

}
