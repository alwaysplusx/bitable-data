package com.harmony.bitable.repository.support

import com.harmony.bitable.core.BitableEntityInformation
import com.harmony.bitable.core.BitableOperations
import com.harmony.bitable.oapi.Pageable
import com.harmony.bitable.oapi.cursor.PageCursor
import com.harmony.bitable.repository.BitableRepository
import com.harmony.bitable.repository.FilterCustomizer
import org.springframework.data.util.Streamable
import java.util.*

class SimpleBitableRepository<T : Any, ID : Any>(
    private val entityInformation: BitableEntityInformation<T, ID>,
    private val bitableOperations: BitableOperations,
) : BitableRepository<T, ID> {

    override fun <S : T> update(entity: S) = bitableOperations.update(entity)

    override fun <S : T> save(entity: S) = bitableOperations.insert(entity)

    override fun <S : T> saveAll(entities: Iterable<S>): Iterable<S> {
        return entities.chunked(1000).flatMap {
            bitableOperations.insertBatch(entityInformation.javaType as Class<S>, it)
        }
    }

    override fun findById(id: ID): Optional<T> {
        val entity = bitableOperations.findById(id, entityInformation.javaType) ?: return Optional.empty()
        return Optional.of(entity)
    }

    override fun existsById(id: ID): Boolean {
        return findById(id).isPresent
    }

    override fun findAll(): Iterable<T> = bitableOperations.findAll(entityInformation.javaType)

    override fun count(): Long = bitableOperations.count(entityInformation.javaType)

    override fun deleteAll() = bitableOperations.delete(entityInformation.javaType)

    override fun deleteAll(entities: Iterable<T>) {
        for (entity in entities) {
            bitableOperations.delete(entity)
        }
    }

    override fun deleteAllById(ids: Iterable<ID>) {
        ids.forEach { bitableOperations.delete(it, entityInformation.javaType) }
    }

    override fun delete(entity: T) {
        bitableOperations.delete(entity)
    }

    override fun deleteById(id: ID) {
        bitableOperations.delete(id, entityInformation.javaType)
    }

    override fun findAllById(ids: Iterable<ID>): Iterable<T> {
        return Streamable.of(ids).map { findById(it).orElse(null) }.filterNotNull()
    }

    override fun getOne(filterCustomizer: FilterCustomizer): T {
        return bitableOperations.getOne(entityInformation.javaType, filterCustomizer::customize)
    }

    override fun findFirst(filterCustomizer: FilterCustomizer): T? {
        return bitableOperations.findFirst(entityInformation.javaType, filterCustomizer::customize)
    }

    override fun search(filterCustomizer: FilterCustomizer): PageCursor<T> {
        return bitableOperations.scan(entityInformation.javaType, Pageable(), filterCustomizer::customize)
    }

    override fun count(filterCustomizer: FilterCustomizer): Long {
        return bitableOperations.count(entityInformation.javaType, filterCustomizer::customize)
    }

}
