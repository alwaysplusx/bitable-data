package com.harmony.bitable.core

import com.harmony.bitable.convert.BitableConverter
import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.oapi.BitableRecordApi
import com.harmony.bitable.oapi.Pageable
import com.harmony.bitable.oapi.cursor.*
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReq
import com.lark.oapi.service.bitable.v1.model.SearchAppTableRecordReqBody
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.util.ClassUtils

/**
 * 支持多维表格行数据操作
 * @see BitableOperations
 */
class BitableTemplate(
    private val bitableRecordApi: BitableRecordApi,
    private val bitableMappingContext: BitableMappingContext,
    private val bitableConverter: BitableConverter,
) : BitableOperations {

    override fun <T : Any> insert(instance: T): T {
        val persistentEntity = getPersistentEntity(instance)
        val record = convertToRecord(instance)
        val insertedRecord = bitableRecordApi.create(persistentEntity.getBitableAddress(), record)
        return convertToEntity(insertedRecord, persistentEntity)
    }

    override fun <T : Any> insertBatch(instances: Iterable<T>, domainType: Class<T>): Iterable<T> {
        val persistentEntity = getPersistentEntity(domainType)
        val records = instances.map { convertToRecord(it) }
        val address = persistentEntity.getBitableAddress()
        return bitableRecordApi.batchCreate(address, records).map { convertToEntity(it, persistentEntity) }
    }

    override fun <T : Any> update(instance: T): T {
        val persistentEntity = getPersistentEntity(instance)
        val recordId = persistentEntity.getRecordIdAccessor(instance).getRequiredRecordId()
        val record = convertToRecord(instance).apply {
            this.recordId = recordId
        }
        val updatedRecord = bitableRecordApi.update(persistentEntity.getBitableAddress(), record)
        return convertToEntity(updatedRecord, persistentEntity)
    }

    override fun <T : Any> deleteAll(domainType: Class<T>) {
        val persistentEntity = getPersistentEntity(domainType)
        val recordIds = bitableRecordApi.search(persistentEntity.getBitableAddress())
            .steamOfElements()
            .map { it.recordId }
            .toList()
        deleteAllById(recordIds, domainType)
    }

    override fun <T : Any> delete(instance: T): Boolean {
        val persistentEntity = getPersistentEntity(instance)
        val recordId = persistentEntity.getRecordIdAccessor(instance).getRequiredRecordId()
        return bitableRecordApi.delete(persistentEntity.getBitableAddress(), recordId)
    }

    override fun <T : Any> deleteById(recordId: String, domainType: Class<T>): Boolean {
        val persistentEntity = getPersistentEntity(domainType)
        return bitableRecordApi.delete(persistentEntity.getBitableAddress(), recordId)
    }

    override fun <T : Any> deleteAllById(recordIds: Iterable<String>, domainType: Class<T>): Map<String, Boolean> {
        val persistentEntity = getPersistentEntity(domainType)
        return recordIds.chunked(500) {
            bitableRecordApi.batchDelete(persistentEntity.getBitableAddress(), it)
        }.flatMap { it.entries }.associate { it.toPair() }
    }

    override fun <T : Any> count(
        domainType: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit
    ): Long {
        val persistentEntity = getPersistentEntity(domainType)
        return bitableRecordApi.count(persistentEntity.getBitableAddress(), searchCustomizer).toLong()
    }

    override fun <T : Any> findById(recordId: String, domainType: Class<T>): T? {
        val persistentEntity = getPersistentEntity(domainType)
        val record = bitableRecordApi.get(persistentEntity.getBitableAddress(), recordId) ?: return null
        return convertToEntity(record, persistentEntity)
    }

    override fun <T : Any> findAllById(recordIds: Iterable<String>, domainType: Class<T>): Iterable<T> {
        val persistentEntity = getPersistentEntity(domainType)
        return bitableRecordApi.batchGet(persistentEntity.getBitableAddress(), recordIds.toList())
            .map { convertToEntity(it, persistentEntity) }
    }

    override fun <T : Any> scan(
        domainType: Class<T>,
        pageable: Pageable,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit
    ): PageCursor<T> {
        val persistentEntity = getPersistentEntity(domainType)
        return bitableRecordApi.search(
            persistentEntity.getBitableAddress(),
            pageable,
            searchCustomizer
        ).convert { convertToEntity(it, persistentEntity) }
    }

    override fun <T : Any> findOne(
        domainType: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit
    ): T {
        val persistentEntity = getPersistentEntity(domainType)
        val record = getUniqueRecord(persistentEntity, searchCustomizer)
        return convertToEntity(record, persistentEntity)
    }

    private fun <T : Any> getUniqueRecord(
        persistentEntity: BitablePersistentEntity<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit
    ): AppTableRecord {
        val matchedPageSlice: PageSlice<AppTableRecord>? = bitableRecordApi.search(
            persistentEntity.getBitableAddress(),
            Pageable(2),
            searchCustomizer
        ).nextSliceOrNull()
        if (matchedPageSlice == null || matchedPageSlice.total() == 0) {
            throw IncorrectResultSizeDataAccessException(1, 0)
        }
        if (matchedPageSlice.total() > 1) {
            throw IncorrectResultSizeDataAccessException(1, matchedPageSlice.total())
        }
        return matchedPageSlice.firstElement()
    }

    private fun <R> getPersistentEntity(cls: Class<R>): BitablePersistentEntity<R> {
        return bitableMappingContext.getPersistentEntity(cls) as BitablePersistentEntity<R>
    }

    private fun <R> getPersistentEntity(obj: R): BitablePersistentEntity<R> {
        val entityType = if (obj is Class<*>) obj else ClassUtils.getUserClass(obj as Any)
        return bitableMappingContext.getPersistentEntity(entityType) as BitablePersistentEntity<R>
    }

    /**
     * 将实体数据转化为多维表格的 record
     */
    private fun <R> convertToRecord(obj: R): AppTableRecord {
        val record = AppTableRecord()
        bitableConverter.write(obj as Any, record)
        return record
    }

    /**
     * 将多维表格的 record 转化为实体数据
     */
    private fun <R> convertToEntity(record: AppTableRecord, persistentEntity: BitablePersistentEntity<R>): R {
        return bitableConverter.read(persistentEntity.type, record)
    }

}
