package com.harmony.bitable.utils

import com.harmony.bitable.BitableAddress
import com.harmony.bitable.core.SearchBodyBuilder
import com.harmony.bitable.core.SearchRequest
import com.harmony.bitable.core.SearchRequestBuilder
import com.harmony.bitable.oapi.Pageable
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody

/**
 * @author wuxin
 */
object SearchUtils {

    /**
     * 聚合 customizer，按顺序覆盖前置 customizer 的配置
     */
    fun all(
        first: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit,
        second: ((req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit) = { _, _ -> }
    ) = { req: SearchRequestBuilder, body: SearchBodyBuilder ->
        first(req, body)
        second(req, body)
    }

    fun buildSearchRequest(
        address: BitableAddress,
        defaultPageable: Pageable = Pageable(),
        customizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit = { _, _ -> }
    ): SearchRequest {
        val requestBuilder = SearchAppTableRecordReq.newBuilder()
        val bodyBuilder = SearchAppTableRecordReqBody.newBuilder()
        customizer(requestBuilder, bodyBuilder)

        val searchRequest = requestBuilder
            .appToken(address.appToken)
            .tableId(address.tableId)
            .searchAppTableRecordReqBody(bodyBuilder.build())
            .build()

        if (searchRequest.pageSize == null) {
            searchRequest.pageSize = defaultPageable.pageSize
        }
        if (searchRequest.pageToken.isNullOrBlank()) {
            searchRequest.pageToken = defaultPageable.pageToken
        }
        return searchRequest
    }

}