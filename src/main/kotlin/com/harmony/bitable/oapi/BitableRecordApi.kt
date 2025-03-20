package com.harmony.bitable.oapi

import com.harmony.bitable.BitableAddress
import com.harmony.bitable.core.SearchRequest
import com.harmony.bitable.oapi.cursor.PageCursor
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.BatchGetAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.BatchGetAppTableRecordReqBody
import com.lark.oapi.service.bitable.v1.model.UpdateAppTableRecordReq

/**
 * @author wuxin
 */
interface BitableRecordApi {

    /**
     * 创建记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.create
     */
    fun create(address: BitableAddress, record: AppTableRecord, userIdType: String? = null): AppTableRecord

    /**
     * 批量创建记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchCreate
     */
    fun batchCreate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String? = null,
    ): List<AppTableRecord>

    /**
     * 删除记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.delete
     */
    fun delete(address: BitableAddress, recordId: String): Boolean

    /**
     * 批量删除记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchDelete
     */
    fun batchDelete(address: BitableAddress, recordIds: List<String>): Map<String, Boolean>

    /**
     * 更新记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.update
     */
    fun update(address: BitableAddress, record: AppTableRecord, userIdType: String? = null): AppTableRecord

    /**
     * 更新记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.update
     */
    fun update(request: UpdateAppTableRecordReq): AppTableRecord

    /**
     * 批量更新记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchUpdate
     */
    fun batchUpdate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String? = null,
    ): List<AppTableRecord>

    /**
     * 获取记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.get
     */
    fun get(address: BitableAddress, recordId: String, userIdType: String? = null): AppTableRecord? =
        batchGet(address, listOf(recordId), userIdType).firstOrNull()

    /**
     * 批量获取记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchGet
     */
    fun batchGet(address: BitableAddress, recordIds: List<String>, userIdType: String? = null): List<AppTableRecord> {
        val body = BatchGetAppTableRecordReqBody
            .newBuilder()
            .recordIds(recordIds.toTypedArray())
            .userIdType(userIdType)
            .build()
        val request = BatchGetAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .batchGetAppTableRecordReqBody(body)
            .build()
        return batchGet(request)
    }

    /**
     * 批量获取记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchGet
     */
    fun batchGet(request: BatchGetAppTableRecordReq): List<AppTableRecord>

    /**
     * 搜索记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.search
     */
    fun search(request: SearchRequest): PageCursor<AppTableRecord>

    /**
     * 统计数据量
     */
    fun count(request: SearchRequest): Int

}