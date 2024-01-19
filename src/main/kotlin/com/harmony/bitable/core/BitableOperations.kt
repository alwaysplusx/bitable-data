package com.harmony.bitable.core

import com.harmony.bitable.filter.RecordFilter
import com.harmony.bitable.oapi.PageCursor

interface BitableOperations {

    fun <T : Any> insert(objectToInsert: T): T

    fun <T : Any> update(objectToUpdate: T): T

    fun delete(type: Class<*>)

    fun <T : Any> delete(objectToDelete: T): T

    fun <T : Any> delete(id: Any, type: Class<T>): T

    fun count(type: Class<*>): Long

    fun <T : Any> findById(id: Any, type: Class<T>): T?

    fun <T : Any> findAll(type: Class<T>): Iterable<T>

    fun <T : Any> scan(type: Class<T>): PageCursor<T>

    fun <T : Any> findAll(type: Class<T>, recordFilter: RecordFilter): Iterable<T>

    fun <T : Any> scan(type: Class<T>, recordFilter: RecordFilter): PageCursor<T>

}
