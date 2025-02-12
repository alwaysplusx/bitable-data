package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Person

/**
 * @author wuxin
 */
class UpdatedByBitvalReader : BitvalReader {
    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.UPDATED_BY
    }

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.lastModifiedBy ?: record.getPropertyValue(property)
        return ValueConverters.convertObject(value, Person::class.java)
    }
}