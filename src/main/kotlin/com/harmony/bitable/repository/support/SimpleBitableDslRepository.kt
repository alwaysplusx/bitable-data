package com.harmony.bitable.repository.support

import com.harmony.bitable.core.BitableOperations
import com.harmony.bitable.dsl.CountFilterBuilder
import com.harmony.bitable.dsl.SearchFilterBuilder
import com.harmony.bitable.dsl.SingleResultFilterBuilder
import com.harmony.bitable.dsl.UpdateBuilder
import com.harmony.bitable.oapi.cursor.PageCursor
import com.harmony.bitable.repository.BitableDslRepository
import org.springframework.dao.IncorrectResultSizeDataAccessException

class SimpleBitableDslRepository<T : Any>(
    entityInformation: BitableEntityInformation<T>,
    bitableOperations: BitableOperations
) : SimpleBitableRepository<T>(entityInformation, bitableOperations), BitableDslRepository<T> {

    override fun getOne(block: SingleResultFilterBuilder<T>.() -> Unit): T {
        return bitableOperations.findOne(entityInformation.javaType, block)
            ?: throw IncorrectResultSizeDataAccessException(1, 0)
    }

    override fun count(block: CountFilterBuilder<T>.() -> Unit): Long {
        return bitableOperations.count(entityInformation.javaType, block)
    }

    override fun findOne(block: SingleResultFilterBuilder<T>.() -> Unit): T? {
        return bitableOperations.findOne(entityInformation.javaType, block)
    }

    override fun findFirst(block: SingleResultFilterBuilder<T>.() -> Unit): T? {
        return bitableOperations.findFirst(entityInformation.javaType, block)
    }

    override fun search(block: SearchFilterBuilder<T>.() -> Unit): PageCursor<T> {
        return bitableOperations.scan(entityInformation.javaType, block)
    }

    override fun updateById(id: String, block: UpdateBuilder<T>.() -> Unit) {
        bitableOperations.updateById(id, entityInformation.javaType, block)
    }

}