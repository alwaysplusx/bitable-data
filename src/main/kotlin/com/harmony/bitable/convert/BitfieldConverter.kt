package com.harmony.bitable.convert

import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
interface BitfieldConverter {

    /**
     * 读取飞书表格记录中的字段值，并完成类型转换。最终用于写入实体对象。
     */
    fun readAndConvertFieldValueFromRecord(property: BitablePersistentProperty, record: AppTableRecord): Any?

    /**
     * 将值进行转化，并最终写入到飞书表格记录中
     */
    fun convertAndWritePropertyValueToRecord(
        propertyValue: Any?,
        property: BitablePersistentProperty,
        record: AppTableRecord
    )

}