package com.harmony.bitable.repository.support

import com.harmony.bitable.core.BitableOperations
import com.harmony.bitable.oapi.Pageable
import com.harmony.bitable.oapi.cursor.PageCursor
import com.harmony.bitable.repository.BitableRepository
import com.harmony.bitable.repository.FilterCustomizer
import org.springframework.dao.IncorrectResultSizeDataAccessException
import java.util.*

class SimpleBitableRepository<T : Any>(
    private val entityInformation: BitableEntityInformation<T>,
    private val bitableOperations: BitableOperations,
) : BitableRepository<T> {

    override fun <S : T> update(entity: S) = bitableOperations.update(entity)

    override fun <S : T> save(entity: S) = bitableOperations.insert(entity)

    override fun <S : T> saveAll(entities: Iterable<S>): Iterable<S> {
        return entities.chunked(1000).flatMap {
            bitableOperations.insertBatch(it, entityInformation.javaType as Class<S>)
        }
    }

    override fun findById(id: String): Optional<T> {
        val entity = bitableOperations.findById(id, entityInformation.javaType) ?: return Optional.empty()
        return Optional.of(entity)
    }

    override fun existsById(id: String): Boolean {
        return findById(id).isPresent
    }

    override fun findAll(): Iterable<T> = bitableOperations.findAll(entityInformation.javaType)

    override fun count(): Long = bitableOperations.count(entityInformation.javaType)

    override fun deleteAll() = bitableOperations.deleteAll(entityInformation.javaType)

    override fun deleteAll(entities: Iterable<T>) {
        for (entity in entities) {
            bitableOperations.delete(entity)
        }
    }

    override fun deleteAllById(ids: Iterable<String>) {
        ids.chunked(500).forEach {
            bitableOperations.deleteAllById(it, entityInformation.javaType)
        }
    }

    override fun delete(entity: T) {
        bitableOperations.delete(entity)
    }

    override fun deleteById(id: String) {
        bitableOperations.deleteById(id, entityInformation.javaType)
    }

    override fun findAllById(ids: Iterable<String>): Iterable<T> {
        return ids.chunked(100).flatMap {
            bitableOperations.findAllById(ids, entityInformation.javaType)
        }
    }

    override fun getOne(filterCustomizer: FilterCustomizer): T {
        return bitableOperations.findOne(entityInformation.javaType, filterCustomizer::customize)
            ?: throw IncorrectResultSizeDataAccessException(1, 0)
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
