package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Location

/**
 * @author wuxin
 */
class LocationBitvalReader : BitvalReader {
    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.LOCATION
                || property.type.isAssignableFrom(Location::class.java)
    }

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Location? {
        val value = record.getPropertyValue(property) ?: return null
        return ValueConverters.convertObject(value, Location::class.java)
    }

}