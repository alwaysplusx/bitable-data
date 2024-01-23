package com.harmony.bitable.mapping

import com.harmony.bitable.BitableAddress
import com.harmony.bitable.BitityField
import org.springframework.data.mapping.model.MutablePersistentEntity
import org.springframework.data.mapping.model.Property

/**
 * 实体 & 多维表格
 */
interface BitablePersistentEntity<T> : MutablePersistentEntity<T, BitablePersistentProperty> {

    /**
     * 实体所对应的多维表格地址
     */
    fun getBitableAddress(): BitableAddress

    /**
     * 是否存在 [RecordId](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#15d8db94) 字段
     * @see com.harmony.bitable.annotations.BitId
     */
    fun hasRecordIdProperty(): Boolean

    /**
     * accessor, 支持从实体中获取 [RecordId](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#15d8db94) 值
     */
    fun getRecordIdAccessor(bean: Any): RecordIdAccessor

    /**
     * 获取多维表格的行 [RecordId](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bitable/notification#15d8db94) 字段
     */
    fun getRecordIdProperty(): BitablePersistentProperty?

    fun getRequiredRecordIdProperty(): BitablePersistentProperty {
        val recordIdProperty = getRecordIdProperty()
        if (recordIdProperty != null) {
            return recordIdProperty
        }
        throw IllegalStateException(String.format("Required recordId property not found for %s!", type))
    }

    fun getField(property: Property): BitityField?

    fun getRequiredField(property: Property): BitityField {
        val field = getField(property)
        if (field != null) {
            return field
        }
        throw IllegalStateException("Required field ${property.name} not found for ${name}!")
    }

}
