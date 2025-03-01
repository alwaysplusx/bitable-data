package com.harmony.bitable.dsl

import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.utils.SearchUtils
import com.lark.oapi.service.bitable.v1.model.FilterInfo
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq

/**
 * 支持飞书按字段条件查询(最多只支持一级子查询)
 * https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/reference/bitable-v1/app-table-record/record-filter-guide
 * @see FilterInfo
 */
class SearchFilterBuilder<T : Any>(rootType: Class<T>) : AbstractFilterBuilder<T>(rootType) {

    fun paging(pageSize: Int, offset: String) {
        this.pageSize = pageSize
        this.offset = offset
    }

    override fun build(persistentEntity: BitablePersistentEntity<T>): SearchAppTableRecordReq {
        val address = persistentEntity.getBitableAddress()
        return SearchUtils.buildSearchRequest(address) { req, body ->
            customizer(req, body)

            val searchFieldNames = columns.map { persistentEntity.getFieldByName(it).getBitfieldName() }.toTypedArray()
            val searchFilter = conditionBuilder.buildSearchFilter(persistentEntity)
            val searchSorts = buildSearchSorts(persistentEntity)
            body.fieldNames(searchFieldNames)
                .filter(searchFilter)
                .sort(searchSorts.toTypedArray())
                .viewId(viewId)

            req.pageSize(pageSize).pageToken(offset)
        }
    }

}

