package com.harmony.bitable.repository

import com.harmony.bitable.filter.RecordFilter
import com.harmony.bitable.filter.SimpleRecordFilter
import com.harmony.bitable.oapi.PageCursor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BitableRepository<T : Any, ID> : CrudRepository<T, ID> {

    fun <S : T> update(entity: S): S

    fun scan(recordFilter: RecordFilter = SimpleRecordFilter()): PageCursor<T>

    fun <S : T> getOne(filter: String): S?

    fun count(filter: String): Long

}
