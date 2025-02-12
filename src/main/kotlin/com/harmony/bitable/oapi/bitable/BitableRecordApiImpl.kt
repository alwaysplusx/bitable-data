package com.harmony.bitable.oapi.bitable

import com.harmony.bitable.BitableAddress
import com.harmony.bitable.oapi.BitableRecordApi
import com.harmony.bitable.oapi.Pageable
import com.harmony.bitable.oapi.cursor.PageCursor
import com.harmony.bitable.oapi.ensureData
import com.lark.oapi.Client
import com.lark.oapi.core.request.RequestOptions
import com.lark.oapi.service.bitable.v1.model.*

/**
 * 多维表格行数据管理(增删改查)
 * @author wuxin
 */
class BitableRecordApiImpl(client: Client, private val pageSize: Int = 20) : BitableRecordApi {

    private val appTableRecordClient = client.bitable().appTableRecord()

    /**
     * 插入飞书表格行数据
     */
    override fun create(
        address: BitableAddress,
        record: AppTableRecord,
        userIdType: String?,
        options: RequestOptions,
    ): AppTableRecord {
        val request = CreateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .appTableRecord(record)
            .build()
        return appTableRecordClient.create(request, options).ensureData().record
    }

    /**
     * 批量插入飞书多维表格数据
     */
    override fun batchCreate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String?,
        options: RequestOptions,
    ): List<AppTableRecord> {

        val body = BatchCreateAppTableRecordReqBody.newBuilder()
            .records(records.toTypedArray())
            .build()

        val request = BatchCreateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .batchCreateAppTableRecordReqBody(body)
            .build()

        return appTableRecordClient.batchCreate(request, options).ensureData().records.toList()
    }

    /**
     * 依据 recordId 删除飞书多维表格行数据
     */
    override fun delete(
        address: BitableAddress,
        recordId: String,
        options: RequestOptions,
    ): Boolean {

        val request = DeleteAppTableRecordReq.newBuilder()
            .recordId(recordId)
            .appToken(address.appToken)
            .tableId(address.tableId)
            .build()

        return appTableRecordClient.delete(request, options).ensureData().deleted
    }

    /**
     *  依据 recordId 批量删除飞书多维表格行数据
     */
    override fun batchDelete(
        address: BitableAddress,
        recordIds: List<String>,
        options: RequestOptions,
    ): Map<String, Boolean> {

        val body = BatchDeleteAppTableRecordReqBody.newBuilder()
            .records(recordIds.toTypedArray())
            .build()

        val request = BatchDeleteAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .batchDeleteAppTableRecordReqBody(body)
            .build()

        return appTableRecordClient.batchDelete(request, options)
            .ensureData()
            .records
            .associate { it.recordId to it.deleted }
    }

    /**
     * 更新多维表格数据
     */
    override fun update(
        address: BitableAddress,
        record: AppTableRecord,
        userIdType: String?,
        options: RequestOptions,
    ): AppTableRecord {

        requireNotNull(record.recordId) { "recordId not allow null" }
        val request = UpdateAppTableRecordReq.newBuilder()
            .recordId(record.recordId)
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .appTableRecord(record)
            .build()

        return appTableRecordClient.update(request, options).ensureData().record
    }

    override fun batchUpdate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String?,
        options: RequestOptions,
    ): List<AppTableRecord> {

        val body = BatchUpdateAppTableRecordReqBody.newBuilder()
            .records(records.toTypedArray())
            .build()

        val request = BatchUpdateAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .userIdType(userIdType)
            .batchUpdateAppTableRecordReqBody(body)
            .build()

        return appTableRecordClient.batchUpdate(request, options).ensureData().records.toList()
    }

    override fun batchGet(
        address: BitableAddress,
        recordIds: List<String>,
        options: RequestOptions,
        customizer: (BatchGetAppTableRecordReq.Builder, BatchGetAppTableRecordReqBody.Builder) -> Unit
    ): List<AppTableRecord> {
        val requestBuilder = BatchGetAppTableRecordReq.newBuilder()
        val bodyBuilder = BatchGetAppTableRecordReqBody.newBuilder()
        customizer(requestBuilder, bodyBuilder)

        val request = requestBuilder
            .appToken(address.appToken)
            .tableId(address.tableId)
            .batchGetAppTableRecordReqBody(bodyBuilder.recordIds(recordIds.toTypedArray()).build())
            .build()
        return appTableRecordClient.batchGet(request).ensureData().records.toList()
    }

    override fun list(address: BitableAddress, options: RequestOptions) = search(address, Pageable(pageSize), options)

    override fun search(
        address: BitableAddress,
        pageable: Pageable,
        options: RequestOptions,
        customizer: (SearchAppTableRecordReq.Builder, SearchAppTableRecordReqBody.Builder) -> Unit
    ): PageCursor<AppTableRecord> {
        val requestBuilder = SearchAppTableRecordReq.newBuilder()
        val bodyBuilder = SearchAppTableRecordReqBody.newBuilder()
        customizer(requestBuilder, bodyBuilder)

        val request = requestBuilder
            .appToken(address.appToken)
            .tableId(address.tableId)
            .pageSize(pageable.pageSize)
            .pageToken(pageable.pageToken)
            .searchAppTableRecordReqBody(bodyBuilder.build())
            .build()
        return appTableRecordClient.searchCursor(request)
    }

    override fun count(
        address: BitableAddress,
        options: RequestOptions,
        customizer: (SearchAppTableRecordReq.Builder, SearchAppTableRecordReqBody.Builder) -> Unit
    ): Int {
        val requestBuilder = SearchAppTableRecordReq.newBuilder()
        val bodyBuilder = SearchAppTableRecordReqBody.newBuilder()
        customizer(requestBuilder, bodyBuilder)

        val request = requestBuilder.appToken(address.appToken)
            .tableId(address.tableId)
            .pageSize(1)
            .searchAppTableRecordReqBody(bodyBuilder.build())
            .build()
        return appTableRecordClient.search(request).ensureData().total
    }

}
