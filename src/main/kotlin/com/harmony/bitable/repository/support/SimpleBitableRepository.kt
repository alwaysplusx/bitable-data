package com.harmony.bitable.repository.support

import com.harmony.bitable.core.BitableEntityInformation
import com.harmony.bitable.core.BitableOperations
import com.harmony.bitable.filter.*
import com.harmony.bitable.oapi.PageCursor
import com.harmony.bitable.oapi.Pageable
import com.harmony.bitable.oapi.first
import com.harmony.bitable.oapi.firstOrNull
import com.harmony.bitable.repository.BitableRepository
import org.springframework.data.util.Streamable
import java.util.*

class SimpleBitableRepository<T : Any, ID : Any>(
    private val entityInformation: BitableEntityInformation<T, ID>,
    private val bitableOperations: BitableOperations,
) : BitableRepository<T, ID> {

    private val nameProvider: NameProvider = FilterBuilder.buildNameProvider(entityInformation.javaType)

    override fun <S : T> save(entity: S) = bitableOperations.insert(entity)

    override fun <S : T> saveAll(entities: Iterable<S>): Iterable<S> {
        return Streamable.of(entities).map { save(entity = it) }
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
        for (id in ids) {
            bitableOperations.delete(id, entityInformation.javaType)
        }
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

    override fun scan(pageable: Pageable, closure: PredicateBuilder<T>.() -> Unit): PageCursor<T> {
        val filter = buildRecordFilter(pageable, closure)
        return scan(filter)
    }

    override fun scan(recordFilter: RecordFilter): PageCursor<T> {
        return bitableOperations.scan(entityInformation.javaType, recordFilter)
    }

    override fun firstOrNull(closure: PredicateBuilder<T>.() -> Unit): T? {
        val filter = buildRecordFilter(Pageable(1), closure)
        return scan(filter).firstOrNull()
    }

    override fun first(closure: PredicateBuilder<T>.() -> Unit): T {
        val filter = buildRecordFilter(Pageable(1), closure)
        return scan(filter).first()
    }

    override fun filter(closure: RecordFilterBuilder<T>.() -> Unit): PageCursor<T> {
        val builder = RecordFilterBuilder(entityInformation.javaType)
        closure(builder)
        return scan(builder.build(nameProvider))
    }

    private fun buildRecordFilter(pageable: Pageable, closure: PredicateBuilder<T>.() -> Unit): RecordFilter {
        val builder = PredicateBuilder(entityInformation.javaType)
        closure(builder)
        val filter = builder.build(nameProvider)
        return SimpleRecordFilter(filter = filter, pageable = pageable)
    }

}
