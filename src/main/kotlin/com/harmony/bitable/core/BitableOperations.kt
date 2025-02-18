package com.harmony.bitable.core

import com.harmony.bitable.oapi.Pageable
import com.harmony.bitable.oapi.cursor.PageCursor
import com.harmony.bitable.oapi.cursor.firstElementOrNull
import com.harmony.bitable.oapi.cursor.toElementList
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody

/**
 * 支持 bitable 数据的操作
 */
interface BitableOperations {

    fun <T : Any> insert(instance: T): T

    fun <T : Any> insertBatch(instances: Iterable<T>, domainType: Class<T>): Iterable<T>

    fun <T : Any> update(instance: T): T

    fun <T : Any> deleteAll(domainType: Class<T>)

    fun <T : Any> delete(instance: T): Boolean

    fun <T : Any> deleteById(recordId: String, domainType: Class<T>): Boolean

    fun <T : Any> deleteAllById(recordIds: Iterable<String>, domainType: Class<T>): Map<String, Boolean>

    fun <T : Any> findById(recordId: String, domainType: Class<T>): T?

    fun <T : Any> findAll(domainType: Class<T>): Iterable<T> = scan(domainType).toElementList()

    fun <T : Any> findAllById(recordIds: Iterable<String>, domainType: Class<T>): Iterable<T>

    fun <T : Any> findOne(
        domainType: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit = { _, _ -> }
    ): T?

    fun <T : Any> findFirst(
        domainType: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit = { _, _ -> }
    ): T? = scan(domainType, Pageable(1), searchCustomizer).firstElementOrNull()

    fun <T : Any> scan(
        domainType: Class<T>,
        pageable: Pageable = Pageable(),
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit = { _, _ -> }
    ): PageCursor<T>

    fun <T : Any> count(
        domainType: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit = { _, _ -> }
    ): Long

}
