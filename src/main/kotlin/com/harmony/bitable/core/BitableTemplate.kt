package com.harmony.bitable.core

import com.harmony.bitable.convert.BitableConverter
import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.oapi.BitableRecordApi
import com.harmony.bitable.oapi.LarkException
import com.harmony.bitable.oapi.Pageable
import com.harmony.bitable.oapi.cursor.*
import com.lark.oapi.core.request.RequestOptions
import com.lark.oapi.service.bitable.v1.enums.ConditionOperatorEnum
import com.lark.oapi.service.bitable.v1.model.*
import org.springframework.dao.DataIntegrityViolationException
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

    override fun <T : Any> insert(objectToInsert: T): T {
        val persistentEntity = getPersistentEntity(objectToInsert)
        val record = convertToRecord(objectToInsert)
        val insertedRecord = bitableRecordApi.create(persistentEntity.getBitableAddress(), record)
        return convertToEntity(insertedRecord, persistentEntity)
    }

    override fun <T : Any> insertBatch(type: Class<T>, objectsToInsert: Iterable<T>): Iterable<T> {
        val persistentEntity = getPersistentEntity(type)
        val records = objectsToInsert.map { convertToRecord(it) }
        val address = persistentEntity.getBitableAddress()
        return bitableRecordApi.batchCreate(address, records).map { convertToEntity(it, persistentEntity) }.toList()
    }

    override fun <T : Any> update(objectToUpdate: T): T {
        val persistentEntity = getPersistentEntity(objectToUpdate)
        val recordId: String? = persistentEntity.getRecordIdAccessor(objectToUpdate).getRecordId()
        if (recordId != null) {
            return updateByRecordId(recordId, objectToUpdate)
        }
        val id: Any = persistentEntity.getIdentifierAccessor(objectToUpdate as Any).identifier
            ?: throw IllegalStateException("id must not be null")
        return updateById(id, objectToUpdate)
    }

    private fun <T : Any> updateById(id: Any, objectToUpdate: T): T {
        val persistentEntity = getPersistentEntity(objectToUpdate)
        if (persistentEntity.requiredIdProperty.isRecordIdProperty()) {
            return updateByRecordId(id.toString(), objectToUpdate)
        }
        val record = getRecordById(id, persistentEntity)
        return updateByRecordId(record.recordId, objectToUpdate)
    }

    private fun <T : Any> updateByRecordId(recordId: String, objectToUpdate: T): T {
        val persistentEntity = getPersistentEntity(objectToUpdate)
        val record = convertToRecord(objectToUpdate).apply {
            this.recordId = recordId
        }
        val updatedRecord = bitableRecordApi.update(persistentEntity.getBitableAddress(), record)
        return convertToEntity(updatedRecord, persistentEntity)
    }

    override fun delete(type: Class<*>) {
        val persistentEntity = getPersistentEntity(type)
        bitableRecordApi.list(persistentEntity.getBitableAddress())
            .steamOfElements()
            .forEach {
                deleteByRecord(it, persistentEntity)
            }
    }

    override fun <T : Any> delete(objectToDelete: T): T {
        val persistentEntity = getPersistentEntity(objectToDelete)
        val recordId = persistentEntity.getRecordIdAccessor(objectToDelete).getRecordId()
        if (recordId != null) {
            return deleteByRecordId(recordId, persistentEntity)
        }
        val id = persistentEntity.getIdentifierAccessor(objectToDelete as Any).identifier
            ?: throw IllegalStateException("id must not be null")
        return deleteById(id, persistentEntity)
    }

    override fun <T : Any> delete(id: Any, type: Class<T>) = deleteById(id, getPersistentEntity(type))

    private fun <T : Any> deleteById(id: Any, persistentEntity: BitablePersistentEntity<T>): T {
        if (persistentEntity.requiredIdProperty.isRecordIdProperty()) {
            return deleteByRecordId(id.toString(), persistentEntity)
        }
        val record = getRecordById(id, persistentEntity)
        return deleteByRecord(record, persistentEntity)
    }

    private fun <T : Any> deleteByRecordId(recordId: String, persistentEntity: BitablePersistentEntity<T>): T {
        val record = bitableRecordApi.get(persistentEntity.getBitableAddress(), recordId)
            ?: throw LarkException("$recordId record not found")
        return deleteByRecord(record, persistentEntity)
    }

    private fun <T : Any> deleteByRecord(record: AppTableRecord, persistentEntity: BitablePersistentEntity<T>): T {
        bitableRecordApi.delete(persistentEntity.getBitableAddress(), record.recordId)
        return convertToEntity(record, persistentEntity)
    }

    override fun <T : Any> count(
        type: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit
    ): Long {
        val persistentEntity = getPersistentEntity(type)
        return bitableRecordApi.count(persistentEntity.getBitableAddress(), RequestOptions(), searchCustomizer).toLong()
    }

    override fun <T : Any> findById(id: Any, type: Class<T>): T? {
        val persistentEntity = getPersistentEntity(type)
        if (persistentEntity.requiredIdProperty.isRecordIdProperty()) {
            return findByRecordId(id.toString(), persistentEntity)
        }
        val record = getRecordById(id, persistentEntity)
        return convertToEntity(record, persistentEntity)
    }

    override fun <T : Any> findAll(type: Class<T>): Iterable<T> {
        return scan(type).toElementList()
    }

    override fun <T : Any> scan(type: Class<T>): PageCursor<T> {
        val persistentEntity = getPersistentEntity(type)
        return bitableRecordApi.list(persistentEntity.getBitableAddress())
            .convert { convertToEntity(it, persistentEntity) }
    }

    override fun <T : Any> scan(
        type: Class<T>,
        pageable: Pageable,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit
    ): PageCursor<T> {
        val persistentEntity = getPersistentEntity(type)
        return bitableRecordApi.search(
            persistentEntity.getBitableAddress(),
            pageable,
            RequestOptions(),
            searchCustomizer
        ).convert { convertToEntity(it, persistentEntity) }
    }

    override fun <T : Any> getOne(
        type: Class<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit
    ): T {
        val persistentEntity = getPersistentEntity(type)
        val record = getUniqueRecord(persistentEntity, searchCustomizer)
        return convertToEntity(record, persistentEntity)
    }

    private fun <T : Any> findByRecordId(recordId: String, persistentEntity: BitablePersistentEntity<T>): T? {
        val record = bitableRecordApi.get(persistentEntity.getBitableAddress(), recordId) ?: return null
        return convertToEntity(record, persistentEntity)
    }

    /**
     * 通过自定义的 ID 字段获取飞书多维表格中的 [RecordId](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#15d8db94) 字段值
     */
    private fun <T : Any> getRecordById(id: Any, persistentEntity: BitablePersistentEntity<T>): AppTableRecord {
        val address = persistentEntity.getBitableAddress()
        val record = bitableRecordApi.get(address, id.toString())
        if (record != null) {
            return record
        }
        val recordIdProperty =
            persistentEntity.getRecordIdProperty() ?: throw DataIntegrityViolationException("no recordId property")
        return getUniqueRecord(persistentEntity) { _, body ->
            body.filter(
                FilterInfo.newBuilder()
                    .conditions(
                        arrayOf(
                            Condition.newBuilder()
                                .operator(ConditionOperatorEnum.OPERATORIS)
                                .fieldName(recordIdProperty.getBitfieldName())
                                .value(arrayOf(id.toString()))
                                .build()
                        )
                    )
                    .build()
            )
        }
    }

    private fun <T : Any> getUniqueRecord(
        persistentEntity: BitablePersistentEntity<T>,
        searchCustomizer: (req: SearchAppTableRecordReq.Builder, body: SearchAppTableRecordReqBody.Builder) -> Unit
    ): AppTableRecord {
        val matchedPageSlice: PageSlice<AppTableRecord>? = bitableRecordApi.search(
            persistentEntity.getBitableAddress(),
            Pageable(2),
            RequestOptions(),
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
