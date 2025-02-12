package com.harmony.bitable.oapi

import com.harmony.bitable.BitableAddress
import com.harmony.bitable.oapi.cursor.PageCursor
import com.lark.oapi.core.request.RequestOptions
import com.lark.oapi.service.bitable.v1.model.*
import io.github.resilience4j.ratelimiter.annotation.RateLimiter

/**
 * @author wuxin
 */
interface BitableRecordApi {

    /**
     * 创建记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.create
     */
    @RateLimiter(name = "bitable-record-create")
    fun create(
        address: BitableAddress,
        record: AppTableRecord,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord

    /**
     * 批量创建记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchCreate
     */
    @RateLimiter(name = "bitable-record-batch-create")
    fun batchCreate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): List<AppTableRecord>

    /**
     * 删除记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.delete
     */
    @RateLimiter(name = "bitable-record-delete")
    fun delete(
        address: BitableAddress,
        recordId: String,
        options: RequestOptions = RequestOptions(),
    ): Boolean

    /**
     * 批量删除记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchDelete
     */
    @RateLimiter(name = "bitable-record-batch-delete")
    fun batchDelete(
        address: BitableAddress,
        recordIds: List<String>,
        options: RequestOptions = RequestOptions(),
    ): Map<String, Boolean>

    /**
     * 更新记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.update
     */
    @RateLimiter(name = "bitable-record-update")
    fun update(
        address: BitableAddress,
        record: AppTableRecord,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord

    /**
     * 批量更新记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchUpdate
     */
    @RateLimiter(name = "bitable-record-batch-update")
    fun batchUpdate(
        address: BitableAddress,
        records: List<AppTableRecord>,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): List<AppTableRecord>

    /**
     * 获取记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.get
     */
    @RateLimiter(name = "bitable-record-batch-get")
    fun get(
        address: BitableAddress,
        recordId: String,
        userIdType: String? = null,
        options: RequestOptions = RequestOptions(),
    ): AppTableRecord? = batchGet(address, listOf(recordId), options) { _, body ->
        body.userIdType(userIdType)
    }.firstOrNull()

    /**
     * 批量获取记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.batchGet
     */
    @RateLimiter(name = "bitable-record-batch-get")
    fun batchGet(
        address: BitableAddress,
        recordIds: List<String>,
        options: RequestOptions = RequestOptions(),
        customizer: ((BatchGetAppTableRecordReq.Builder, BatchGetAppTableRecordReqBody.Builder) -> Unit) = { _, _ -> }
    ): List<AppTableRecord>

    @RateLimiter(name = "bitable-record-search")
    fun list(
        address: BitableAddress,
        options: RequestOptions = RequestOptions()
    ): PageCursor<AppTableRecord>

    /**
     * 搜索记录
     * @see com.lark.oapi.service.bitable.v1.resource.AppTableRecord.search
     */
    @RateLimiter(name = "bitable-record-search")
    fun search(
        address: BitableAddress,
        pageable: Pageable = Pageable(),
        options: RequestOptions = RequestOptions(),
        customizer: ((SearchAppTableRecordReq.Builder, SearchAppTableRecordReqBody.Builder) -> Unit) = { _, _ -> }
    ): PageCursor<AppTableRecord>

    /**
     * 统计数据量
     */
    @RateLimiter(name = "bitable-record-search")
    fun count(
        address: BitableAddress,
        options: RequestOptions = RequestOptions(),
        customizer: ((SearchAppTableRecordReq.Builder, SearchAppTableRecordReqBody.Builder) -> Unit) = { _, _ -> }
    ): Int

}