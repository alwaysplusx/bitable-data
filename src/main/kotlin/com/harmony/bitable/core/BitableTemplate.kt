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

        val record = bitableRecordApi.get(persistentEntity.getBitableAddress(), recordId)
            ?: throw LarkException("$recordId record not found")

        return deleteByRecord(record, persistentEntity)
    }

    private fun <T : Any> deleteByRecord(record: AppTableRecord, persistentEntity: BitablePersistentEntity<T>): T {

        bitableRecordApi.delete(persistentEntity.getBitableAddress(), record.recordId)

        return convertToEntity(record, persistentEntity)
    }

    override fun count(type: Class<*>) = bitableRecordApi.count(getPersistentEntity(type).getBitableAddress()).toLong()

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
        return bitableRecordApi.list(persistentEntity.getBitableAddress())
            .convert { convertToEntity(it, persistentEntity) }
    }

    override fun <T : Any> scan(type: Class<T>, recordFilter: RecordFilter): PageCursor<T> {
        val persistentEntity = getPersistentEntity(type)
        val address = persistentEntity.getBitableAddress()
        val request = ListAppTableRecordReq.newBuilder()
            .appToken(address.appToken)
            .tableId(address.tableId)
            .pageSize(recordFilter.getPageSize())
            .pageToken(recordFilter.getPageToken())
            .viewId(recordFilter.getViewId())
            .filter(recordFilter.getFilter())
            .sort(recordFilter.getSort())
            .fieldNames(recordFilter.getFieldNames())
            .build()
        return bitableRecordApi.list(request).convert { convertToEntity(it, persistentEntity) }
    }

    private fun <T : Any> findByRecordId(recordId: String, persistentEntity: BitablePersistentEntity<T>): T? {
        val record = bitableRecordApi.get(persistentEntity.getBitableAddress(), recordId) ?: return null
        return convertToEntity(record, persistentEntity)
    }

    /**
     * 通过自定义的 ID 字段获取飞书多维表格中的 [RecordId](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#15d8db94) 字段值
     */
    private fun getRecordById(id: Any, persistentEntity: BitablePersistentEntity<*>): AppTableRecord {
        val idFilter = idFilter(id, persistentEntity.requiredIdProperty)
        return bitableRecordApi.getOne(persistentEntity.getBitableAddress(), idFilter)
            ?: throw LarkException("$id record not found")
    }

    /**
     * 通过自定义Id 构建筛选器 [filter](https://open.feishu.cn/document/server-docs/docs/bitable-v1/app-table-record/filter)
     */
    private fun idFilter(id: Any, property: BitablePersistentProperty): String {
        val idValue = (id as? Number)?.toString() ?: "\"$id\""
        return String.format("CurrentValue.[%s]=%s", property.getBitfieldName(), idValue)
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
