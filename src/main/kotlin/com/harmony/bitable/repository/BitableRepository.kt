package com.harmony.bitable.repository

import com.harmony.bitable.oapi.cursor.PageCursor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BitableRepository<T : Any, ID> : CrudRepository<T, ID> {

    fun <S : T> update(entity: S): S

    fun getOne(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): T

    fun findFirst(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): T?

    fun search(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): PageCursor<T>

    fun count(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): Long

}