package com.harmony.bitable.core

import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.utils.SearchUtils

class CountFilterBuilder<T>(
    rootType: Class<T>,
    conjunction: Conjunction = Conjunction.AND,
) : ConditionBuilder<T>(rootType, conjunction) {

    private var viewId: String? = null
    private var customizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit = { _, _ -> }

    internal fun build(persistentEntity: BitablePersistentEntity<T>): SearchRequest {
        val address = persistentEntity.getBitableAddress()
        return SearchUtils.buildSearchRequest(address) { req, body ->
            customizer(req, body)

            val searchFilter = this.buildSearchFilter(persistentEntity)
            body.filter(searchFilter)
                .sort(null)
                .viewId(viewId)

            req.pageSize(1)
        }
    }

}