package com.harmony.bitable.repository

import com.harmony.bitable.dsl.CountFilterBuilder
import com.harmony.bitable.dsl.SearchFilterBuilder
import com.harmony.bitable.dsl.SingleResultFilterBuilder
import com.harmony.bitable.dsl.UpdateBuilder
import com.harmony.bitable.oapi.cursor.PageCursor
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BitableDslRepository<T : Any> : BitableRepository<T> {

    /**
     * update by id
     */
    fun updateById(id: String, block: UpdateBuilder<T>.() -> Unit)

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