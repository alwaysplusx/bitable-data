package com.harmony.bitable.core

import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.utils.SearchUtils
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq

class SingleResultFilterBuilder<T : Any>(rootType: Class<T>) : AbstractFilterBuilder<T>(rootType) {

    init {
        this.pageSize = 1
    }

    fun offset(offset: String?) {
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