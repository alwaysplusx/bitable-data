package com.harmony.bitable.filter

import com.harmony.bitable.oapi.Pageable


/**
 * @author wuxin
 * @see com.lark.oapi.service.bitable.v1.model.ListAppTableRecordReq
 */
class SimpleRecordFilter(
    private val filter: String? = null,
    private val pageable: Pageable = Pageable(),
    private val fieldNames: String? = null,
    private val viewId: String? = null,
    private val sort: String? = null,
    private val pageSize: Int = pageable.pageSize,
    private val pageToken: String? = pageable.pageToken,
) : RecordFilter {

    override fun getPageSize(): Int = pageSize

    override fun getPageToken(): String? = pageToken

    override fun getFieldNames(): String? = fieldNames

    override fun getViewId(): String? = viewId

    override fun getFilter(): String? = filter

    override fun getSort(): String? = sort

}
