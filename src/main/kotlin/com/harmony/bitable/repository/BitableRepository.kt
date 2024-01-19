package com.harmony.bitable.repository

import com.harmony.bitable.filter.PredicateBuilder
import com.harmony.bitable.filter.RecordFilter
import com.harmony.bitable.filter.RecordFilterBuilder
import com.harmony.bitable.oapi.PageCursor
import com.harmony.bitable.oapi.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BitableRepository<T : Any, ID> : CrudRepository<T, ID> {

    companion object {

        val DEFAULT_FIRST_PAGE = Pageable(20)

    }

    fun scan(pageable: Pageable = DEFAULT_FIRST_PAGE, closure: PredicateBuilder<T>.() -> Unit = {}): PageCursor<T>

    fun scan(recordFilter: RecordFilter): PageCursor<T>

    fun filter(closure: RecordFilterBuilder<T>.() -> Unit = {}): PageCursor<T>

    fun firstOrNull(closure: PredicateBuilder<T>.() -> Unit = {}): T?

    fun first(closure: PredicateBuilder<T>.() -> Unit = {}): T

}
