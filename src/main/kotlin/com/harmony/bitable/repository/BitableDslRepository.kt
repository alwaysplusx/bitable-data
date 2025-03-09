package com.harmony.bitable.repository

import com.harmony.bitable.core.CountFilterBuilder
import com.harmony.bitable.core.SearchFilterBuilder
import com.harmony.bitable.core.SingleResultFilterBuilder
import com.harmony.bitable.core.UpdateDslBuilder
import com.harmony.bitable.oapi.cursor.PageCursor
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BitableDslRepository<T : Any> : BitableRepository<T> {

    /**
     * update by id
     */
    fun updateById(id: String, block: UpdateDslBuilder<T>.() -> Unit)

    /**
     * required unique result
     */
    fun getOne(block: SingleResultFilterBuilder<T>.() -> Unit): T

    /**
     * find unique result or null
     */
    fun findOne(block: SingleResultFilterBuilder<T>.() -> Unit): T?

    /**
     * find first result or null
     */
    fun findFirst(block: SingleResultFilterBuilder<T>.() -> Unit): T?

    fun search(block: SearchFilterBuilder<T>.() -> Unit): PageCursor<T>

    fun count(block: CountFilterBuilder<T>.() -> Unit): Long

}