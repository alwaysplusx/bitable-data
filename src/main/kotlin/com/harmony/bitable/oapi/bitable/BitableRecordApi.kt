package com.harmony.bitable.oapi.bitable

import com.harmony.bitable.BitableAddress
import com.harmony.bitable.oapi.PageCursor
import com.harmony.bitable.oapi.ensureData
import com.lark.oapi.Client
import com.lark.oapi.core.request.RequestOptions
import com.lark.oapi.service.bitable.v1.model.*

/**
 * 多维表格行数据管理(增删改查)
 * @author wuxin
 */
class BitableRecordApi(client: Client, private val pageSize: Int = 20) {

    private val appTableRecord = client.bitable().appTableRecord()

    /**
     * 插入飞书表格行数据
     */
    fun create(
        address: BitableAddress,
        record: AppTableRecord,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord {

        val request = CreateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .appTableRecord(record)
            .build()

        return appTableRecord.create(request, options).ensureData().record
    }

    /**
     * 批量插入飞书多维表格数据
     */
    fun batchCreate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): List<AppTableRecord> {

        val recordsBody = BatchCreateAppTableRecordReqBody().apply {
            this.records = records.toTypedArray()
        }

        val request = BatchCreateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .batchCreateAppTableRecordReqBody(recordsBody)
            .build()

        return appTableRecord.batchCreate(request, options).ensureData().records.toList()
    }

    /**
     * 依据 recordId 删除飞书多维表格行数据
     */
    fun delete(
        address: BitableAddress,
        recordId: String,
        options: RequestOptions = RequestOptions(),
    ): Boolean {

        val request = DeleteAppTableRecordReq.newBuilder()
            .recordId(recordId)
            .appToken(address.appToken)
            .tableId(address.tableId)
            .build()

        return appTableRecord.delete(request, options).ensureData().deleted
    }

    /**
     *  依据 recordId 批量删除飞书多维表格行数据
     */
    fun batchDelete(
        address: BitableAddress,
        recordIds: List<String>,
        options: RequestOptions = RequestOptions(),
    ): Map<String, Boolean> {

        val recordIdsBody = BatchDeleteAppTableRecordReqBody().apply {
            this.records = recordIds.toTypedArray()
        }

        val request = BatchDeleteAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .batchDeleteAppTableRecordReqBody(recordIdsBody)
            .build()

        return appTableRecord.batchDelete(request, options)
            .ensureData()
            .records
            .associate { it.recordId to it.deleted }
    }

    /**
     * 更新多维表格数据
     */
    fun update(
        address: BitableAddress,
        record: AppTableRecord,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord {
        requireNotNull(record.recordId) { "recordId not allow null" }
        val request = UpdateAppTableRecordReq.newBuilder()
            .recordId(record.recordId)
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .appTableRecord(record)
            .build()

        return appTableRecord.update(request, options).ensureData().record
    }

    fun batchUpdate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): List<AppTableRecord> {

        val recordsBody = BatchUpdateAppTableRecordReqBody().apply {
            this.records = records.toTypedArray()
        }

        val request = BatchUpdateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .batchUpdateAppTableRecordReqBody(recordsBody)
            .build()

        return appTableRecord.batchUpdate(request, options).ensureData().records.toList()
    }

    fun get(
        address: BitableAddress,
        recordId: String,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord? {

        val request = GetAppTableRecordReq.newBuilder()
            .recordId(recordId)
            .tableId(address.tableId)
            .appToken(address.appToken)
            .userIdType(userIdType)
            .build()

        return get(request, options)
    }

    fun get(request: GetAppTableRecordReq, options: RequestOptions = RequestOptions()): AppTableRecord? {
        return appTableRecord.get(request, options).ensureData().record
    }

    fun getFirst(
        address: BitableAddress,
        filter: String,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord? {
        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .filter(filter)
            .build()
        return getFirst(request, options)
    }

    fun getFirst(request: ListAppTableRecordReq, options: RequestOptions = RequestOptions()): AppTableRecord? {
        val result = getTop2(request, options)
        return if (result.isEmpty()) null else result[0]
    }

    fun getOne(
        address: BitableAddress,
        filter: String,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord? {

        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .filter(filter)
            .build()

        val matched = getTop2(request, options)

        if (matched.isEmpty()) {
            return null
        }

        if (matched.size > 1) {
            throw IllegalStateException()
        }

        return matched[0]
    }


    private fun getTop2(
        request: ListAppTableRecordReq,
        options: RequestOptions = RequestOptions(),
    ): List<AppTableRecord> {
        val result = appTableRecord.list(request.apply { pageSize = 2 }, options).ensureData()
        return if (result.total == 0) emptyList() else result.items.toList()
    }

    fun list(request: ListAppTableRecordReq, options: RequestOptions = RequestOptions()): PageCursor<AppTableRecord> {
        if (request.pageSize == null) {
            request.pageSize = pageSize
        }
        return appTableRecord.listCursor(request, options)
    }

    fun list(address: BitableAddress, options: RequestOptions = RequestOptions()): PageCursor<AppTableRecord> {
        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .pageSize(pageSize)
            .build()
        return list(request, options)
    }

    fun count(address: BitableAddress, options: RequestOptions = RequestOptions()): Int {
        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .pageSize(1)
            .build()
        return appTableRecord.list(request, options).ensureData().total
    }

}
