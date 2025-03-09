package com.harmony.bitable.repository

import com.harmony.bitable.oapi.cursor.PageCursor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BitableRepository<T : Any> : CrudRepository<T, String> {

    fun <S : T> update(entity: S): S

    fun updateById(id: String, fields: Map<String, Any?>)

    fun updateById(id: String, updateCustomizer: UpdateCustomizer<T>)

    fun getOneById(id: String): T

    /**
     * required unique one result
     */
    fun getOne(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): T

    /**
     * find unique one result or null
     */
    fun findOne(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): T?

    /**
     * first one or null
     */
    fun findFirst(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): T?

    fun search(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): PageCursor<T>

    fun count(filterCustomizer: FilterCustomizer = FilterCustomizer.NoOpCustomizer): Long

}