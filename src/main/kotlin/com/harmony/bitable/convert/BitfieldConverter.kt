package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.springframework.data.mapping.PersistentPropertyAccessor

/**
 * @author wuxin
 */
interface BitfieldConverter {

    /**
     * 读取飞书表格记录中的字段值，并完成类型转换。最终用于写入实体对象。
     */
    fun readAndConvertFieldValueFromRecord(property: BitablePersistentProperty, record: AppTableRecord): Any?

    /**
     * 读取实体对象中的字段值，并完成类型转换。最终用于写入飞书表格记录。
     */
    fun readAndConvertPropertyValueFromAccessor(
        property: BitablePersistentProperty,
        accessor: PersistentPropertyAccessor<Any>
    ): Any?

    // fun readValueFromRecord(property: BitablePersistentProperty, record: AppTableRecord): Any?
    // fun readValueFromRecord
    // fun readValueFromAccessor(property: BitablePersistentProperty, accessor: PersistentPropertyAccessor<Any>): Any?

}