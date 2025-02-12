package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class MultiSelectBitvalReader : BitvalReader {

    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.MULTI_SELECT
    }

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property) ?: return null
        val result = ValueConverters.convertArray(value, String::class.java) ?: return null
        // TODO or enum
        return when {
            property.type.isArray -> result
            property.type == List::class.java -> result.toList()
            else -> {
                throw IllegalArgumentException("Unsupported MultiSelect type: ${property.type}")
            }
        }
    }

}