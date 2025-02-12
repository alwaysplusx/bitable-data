package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class CreatedAtBitvalReader : BitvalReader {

    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.CREATED_AT
    }

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.createdTime ?: record.getPropertyValue(property)
        return ValueConverters.convertTime(value, property.type)
    }


}