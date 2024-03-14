package com.harmony.bitable.repository

import com.harmony.bitable.filter.RecordFilter
import com.harmony.bitable.filter.SimpleRecordFilter
import com.harmony.bitable.oapi.PageCursor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BitableRepository<T : Any, ID> : CrudRepository<T, ID> {

    fun update(entity: T): T

    fun scan(recordFilter: RecordFilter = SimpleRecordFilter()): PageCursor<T>

}
