package com.harmony.bitable.dsl

import com.harmony.bitable.core.SearchBodyBuilder
import com.harmony.bitable.core.SearchRequest
import com.harmony.bitable.core.SearchRequestBuilder
import com.harmony.bitable.dsl.builder.ConditionBuilder
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.utils.SearchUtils

class CountFilterBuilder<T>(
    rootType: Class<T>,
    conjunction: Conjunction = Conjunction.AND,
) : ConditionBuilder<T>(rootType, conjunction) {

    private var viewId: String? = null
    private var customizer: (req: SearchRequestBuilder, body: SearchBodyBuilder) -> Unit = { _, _ -> }

    fun build(persistentEntity: BitablePersistentEntity<T>): SearchRequest {
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