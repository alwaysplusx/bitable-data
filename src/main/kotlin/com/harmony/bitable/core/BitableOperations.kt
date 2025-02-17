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

    fun <T : Any> insert(objectToInsert: T): T

    fun <T : Any> insertBatch(type: Class<T>, objectsToInsert: Iterable<T>): Iterable<T>

    fun <T : Any> update(objectToUpdate: T): T

    fun delete(type: Class<*>)

    fun <T : Any> delete(objectToDelete: T): T

    fun <T : Any> delete(id: Any, type: Class<T>): T

    fun <T : Any> findById(id: Any, type: Class<T>): T?

    fun <T : Any> findAll(type: Class<T>): Iterable<T> = scan(type).toElementList()

    fun <T : Any> scan(
        type: Class<T>,
        pageable: Pageable = Pageable(),
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit = { _, _ -> }
    ): PageCursor<T>

    fun <T : Any> getOne(
        type: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit = { _, _ -> }
    ): T

    fun <T : Any> findFirst(
        type: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit = { _, _ -> }
    ): T? = scan(type, Pageable(1), searchCustomizer).firstElementOrNull()

    fun <T : Any> count(
        type: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit = { _, _ -> }
    ): Long

}
