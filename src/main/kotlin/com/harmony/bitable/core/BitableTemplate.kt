package com.harmony.bitable.core

import com.harmony.bitable.convert.BitableConverter
import com.harmony.bitable.filter.RecordFilter
import com.harmony.bitable.mapping.BitableMappingContext
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.*
import com.harmony.bitable.oapi.bitable.BitableRecordApi
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.ListAppTableRecordReq
import org.springframework.util.ClassUtils

class BitableTemplate(
    private val recordApi: BitableRecordApi,
    private val mappingContext: BitableMappingContext,
    private val converter: BitableConverter,
) : BitableOperations {

    companion object {
        const val ID_FILTER = "CurrentValue.[%s]=%s"
    }

    override fun <T : Any> insert(objectToInsert: T): T {
        val persistentEntity = getPersistentEntity(objectToInsert)

        val record = convertToRecord(objectToInsert)

        val insertedRecord = recordApi.create(persistentEntity.getBitableAddress(), record)

        return convertToEntity(insertedRecord, persistentEntity)
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

        val updatedRecord = recordApi.update(persistentEntity.getBitableAddress(), record)
        return convertToEntity(updatedRecord, persistentEntity)
    }

    override fun delete(type: Class<*>) {

        val persistentEntity = getPersistentEntity(type)

        recordApi.list(persistentEntity.getBitableAddress())
            .stream()
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

        val record = recordApi.get(persistentEntity.getBitableAddress(), recordId)
            ?: throw LarkException("$recordId record not found")

        return deleteByRecord(record, persistentEntity)
    }

    private fun <T : Any> deleteByRecord(record: AppTableRecord, persistentEntity: BitablePersistentEntity<T>): T {

        recordApi.delete(persistentEntity.getBitableAddress(), record.recordId)

        return convertToEntity(record, persistentEntity)
    }

    override fun count(type: Class<*>) = recordApi.count(getPersistentEntity(type).getBitableAddress()).toLong()

    override fun <T : Any> findById(id: Any, type: Class<T>): T? {

        val persistentEntity = getPersistentEntity(type)

        if (persistentEntity.requiredIdProperty.isRecordIdProperty()) {
            return findByRecordId(id.toString(), persistentEntity)
        }

        val record = getRecordById(id, persistentEntity)
        return convertToEntity(record, persistentEntity)
    }

    override fun <T : Any> findAll(type: Class<T>): Iterable<T> {
        return scan(type).toList()
    }

    override fun <T : Any> findAll(type: Class<T>, recordFilter: RecordFilter): Iterable<T> {
        return scan(type, recordFilter).toList()
    }

    override fun <T : Any> scan(type: Class<T>): PageCursor<T> {
        val persistentEntity = getPersistentEntity(type)
        return recordApi.list(persistentEntity.getBitableAddress()).convert { convertToEntity(it, persistentEntity) }
    }

    override fun <T : Any> scan(type: Class<T>, recordFilter: RecordFilter): PageCursor<T> {
        val persistentEntity = getPersistentEntity(type)
        val address = persistentEntity.getBitableAddress()
        val request = ListAppTableRecordReq().apply {
            this.pageSize = recordFilter.getPageSize()
            this.pageToken = recordFilter.getPageToken()
            this.viewId = recordFilter.getViewId()
            this.filter = recordFilter.getFilter()
            this.sort = recordFilter.getSort()
            this.fieldNames = recordFilter.getFieldNames()
            this.tableId = address.tableId
            this.appToken = address.appToken
        }
        return recordApi.list(request).convert { convertToEntity(it, persistentEntity) }
    }

    private fun <T : Any> findByRecordId(recordId: String, persistentEntity: BitablePersistentEntity<T>): T? {
        val record = recordApi.get(persistentEntity.getBitableAddress(), recordId) ?: return null
        return convertToEntity(record, persistentEntity)
    }

    private fun getRecordById(id: Any, persistentEntity: BitablePersistentEntity<*>): AppTableRecord {
        val idFilter = idFilter(id, persistentEntity.requiredIdProperty)
        return recordApi.getOne(persistentEntity.getBitableAddress(), idFilter)
            ?: throw LarkException("$id record not found")
    }

    private fun idFilter(id: Any, property: BitablePersistentProperty): String {
        val idValue = (id as? Number)?.toString() ?: "\"$id\""
        return String.format(ID_FILTER, property.getBitfieldName(), idValue)
    }

    private fun <R> getPersistentEntity(cls: Class<R>): BitablePersistentEntity<R> {
        return mappingContext.getPersistentEntity(cls) as BitablePersistentEntity<R>
    }

    private fun <R> getPersistentEntity(obj: R): BitablePersistentEntity<R> {
        val entityType = if (obj is Class<*>) obj else ClassUtils.getUserClass(obj as Any)
        return mappingContext.getPersistentEntity(entityType) as BitablePersistentEntity<R>
    }

    private fun <R> convertToRecord(obj: R): AppTableRecord {
        val record = AppTableRecord()
        converter.write(obj as Any, record)
        return record
    }

    private fun <R> convertToEntity(record: AppTableRecord, persistentEntity: BitablePersistentEntity<R>): R {
        return converter.read(persistentEntity.type, record)
    }

}
